package kr.hhplus.be.server.application.productstatistics.strategy;

import kr.hhplus.be.server.infrastructure.redis.ProductRankRedisRepository;
import org.springframework.stereotype.Component;


@Component
public class DailyRankingStrategy extends AbstractRankingStrategy {
    public DailyRankingStrategy(ProductRankRedisRepository redis) {
        super(redis);
    }

    @Override
    protected String getPrefix() {
        return "ranking:daily:";
    }
}
