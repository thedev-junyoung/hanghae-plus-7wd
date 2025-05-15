package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponAsyncIssueService {

    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;
    private final Clock clock;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public void processAsync(Map<Object, Object> record) {
        Long userId = Long.valueOf((String) record.get("userId"));
        String couponCode = (String) record.get("couponCode");
        String requestId = (String) record.get("requestId");

        // Redis로 멱등성 보장
        String dedupKey = "coupon:issued:" + requestId;
        Boolean isNew = redisTemplate.opsForValue().setIfAbsent(dedupKey, "1", 1, TimeUnit.DAYS);
        if (Boolean.FALSE.equals(isNew)) {
            log.warn("[중복 requestId 감지] requestId={}", requestId);
            return;
        }

        Coupon coupon = couponRepository.findByCode(couponCode);
        if (couponIssueRepository.hasIssued(userId, coupon.getId())) {
            log.info("[이미 발급된 사용자] userId={}, couponCode={}", userId, couponCode);
            return;
        }

        CouponIssue issue = CouponIssue.createAndValidateDecreaseQuantity(userId, coupon, clock);
        couponIssueRepository.save(issue);
        log.info("[쿠폰 발급 성공] userId={}, couponCode={}, requestId={}", userId, couponCode, requestId);
    }
}
