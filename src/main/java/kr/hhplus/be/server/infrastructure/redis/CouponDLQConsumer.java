package kr.hhplus.be.server.infrastructure.redis;

import kr.hhplus.be.server.application.coupon.CouponAsyncIssueService;
import kr.hhplus.be.server.application.coupon.CouponUseCase;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.infrastructure.redis.util.CouponStreamKeyResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponDLQConsumer {

    private final StringRedisTemplate redisTemplate;
    private final CouponAsyncIssueService issueService;

    private static final String CONSUMER_NAME = "dlq-consumer";

    private final CouponUseCase couponService;


    @Scheduled(fixedDelay = 5000)
    public void consumeDLQ() {
        for (String code : couponService.findAllCouponCodes()) {
            String dlqKey = CouponStreamKeyResolver.dlq(code);

            List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream().read(
                    Consumer.from("dlq-group", CONSUMER_NAME),
                    StreamReadOptions.empty().count(5).block(Duration.ofSeconds(2)),
                    StreamOffset.create(dlqKey, ReadOffset.lastConsumed())
            );

            for (MapRecord<String, Object, Object> record : records) {
                try {
                    issueService.processAsync(record.getValue()); // 재시도
                    log.info("[DLQ 재처리 성공] {}", record.getId());
                } catch (Exception e) {
                    log.error("[DLQ 재처리 실패] record={}, error={}", record.getValue(), e.getMessage(), e);
                }
            }
        }

    }
}
