package kr.hhplus.be.server.application.productstatistics.strategy;

import kr.hhplus.be.server.infrastructure.redis.ProductRankRedisRepository;
import org.springframework.stereotype.Component;


@Component
public class WeeklyRankingStrategy extends AbstractRankingStrategy {
    public WeeklyRankingStrategy(ProductRankRedisRepository redis) {
        super(redis);
    }

    @Override
    protected String getPrefix() {
        return "ranking:weekly:";
    }
}

