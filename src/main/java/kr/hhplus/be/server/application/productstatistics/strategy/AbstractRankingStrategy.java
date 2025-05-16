package kr.hhplus.be.server.application.productstatistics.strategy;

import kr.hhplus.be.server.infrastructure.redis.ProductRankRedisRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class AbstractRankingStrategy implements ProductRankingStrategy {

    protected final ProductRankRedisRepository redis;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    protected AbstractRankingStrategy(ProductRankRedisRepository redis) {
        this.redis = redis;
    }

    @Override
    public void record(Long productId, int quantity) {
        String key = getPrefix() + LocalDate.now().format(FORMATTER);
        redis.incrementScore(key, productId, quantity);
        redis.expireIfAbsent(key, getExpireDuration());
    }

    protected abstract String getPrefix();

    protected Duration getExpireDuration() {
        return Duration.ofDays(3);
    }
}
