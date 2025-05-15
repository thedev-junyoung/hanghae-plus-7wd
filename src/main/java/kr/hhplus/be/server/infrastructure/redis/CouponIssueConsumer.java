package kr.hhplus.be.server.infrastructure.redis;

import jakarta.annotation.PostConstruct;
import kr.hhplus.be.server.application.coupon.CouponAsyncIssueService;
import kr.hhplus.be.server.application.coupon.CouponService;
import kr.hhplus.be.server.application.coupon.CouponUseCase;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.infrastructure.redis.util.CouponStreamKeyResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponIssueConsumer {

    private final StringRedisTemplate redisTemplate;
    private final CouponUseCase couponService;
    private final CouponAsyncIssueService issueService;

    private static final String GROUP = "coupon-consumer-group";
    private static final String CONSUMER_NAME = "consumer-1";

    @EventListener(ApplicationReadyEvent.class)
    public void initStreamGroups() {
        for (String code : couponService.findAllCouponCodes()) {
            String streamKey = CouponStreamKeyResolver.resolve(code);
            if (!Boolean.TRUE.equals(redisTemplate.hasKey(streamKey))) {
                redisTemplate.opsForStream().add(streamKey, Map.of("init", "init"));
            }
            try {
                redisTemplate.opsForStream().createGroup(streamKey, GROUP);
            } catch (Exception e) {
                if (!e.getMessage().contains("BUSYGROUP")) throw e;
            }
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void consume() {
        for (String code : couponService.findAllCouponCodes()) {
            String streamKey = CouponStreamKeyResolver.resolve(code);
            List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream().read(
                    Consumer.from(GROUP, CONSUMER_NAME),
                    StreamReadOptions.empty().count(10).block(Duration.ofSeconds(2)),
                    StreamOffset.create(streamKey, ReadOffset.lastConsumed())
            );

            for (MapRecord<String, Object, Object> record : records) {
                try {
                    issueService.processAsync(record.getValue());
                } catch (Exception e) {
                    redisTemplate.opsForStream().add(CouponStreamKeyResolver.dlq(code), record.getValue());
                    log.error("쿠폰 발급 실패. DLQ로 이동 - code={}, error={}", code, e.getMessage(), e);
                }
            }
        }
    }
}
