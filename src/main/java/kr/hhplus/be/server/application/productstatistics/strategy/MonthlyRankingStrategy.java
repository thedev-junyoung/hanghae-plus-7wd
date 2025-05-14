package kr.hhplus.be.server.application.productstatistics.strategy;

import kr.hhplus.be.server.infrastructure.redis.ProductRankRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MonthlyRankingStrategy implements ProductRankingStrategy {

    private final ProductRankRedisRepository redis;
    private static final String KEY = "ranking:monthly";

    @Override
    public void record(Long productId, int quantity) {
        redis.incrementScore(KEY, productId, quantity);
    }
}
