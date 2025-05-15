package kr.hhplus.be.server.infrastructure.redis;

import kr.hhplus.be.server.application.coupon.IssueLimitedCouponCommand;
import kr.hhplus.be.server.infrastructure.redis.util.CouponStreamKeyResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CouponIssueStreamPublisher {

    private final StringRedisTemplate redisTemplate;

    public void publish(IssueLimitedCouponCommand command) {
        String streamKey = CouponStreamKeyResolver.resolve(command.couponCode());

        Map<String, String> payload = Map.of(
                "userId", String.valueOf(command.userId()),
                "couponCode", command.couponCode(),
                "requestId", command.requestId()
        );

        redisTemplate.opsForStream().add(streamKey, payload);
    }
}
