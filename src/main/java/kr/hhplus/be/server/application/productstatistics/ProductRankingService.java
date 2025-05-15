package kr.hhplus.be.server.application.productstatistics;

import kr.hhplus.be.server.application.productstatistics.strategy.ProductRankingStrategy;
import kr.hhplus.be.server.common.vo.Money;
import kr.hhplus.be.server.domain.order.OrderItemRepository;
import kr.hhplus.be.server.infrastructure.redis.ProductRankRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductRankingService {

    private final List<ProductRankingStrategy> strategies;
    private final ProductRankRedisRepository redis;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public void record(Long productId, int quantity) {
        for (ProductRankingStrategy strategy : strategies) {
            strategy.record(productId, quantity);
        }
    }

    public List<Long> getTopN(String periodPrefix, LocalDate date, int limit) {
        String key = buildKey(periodPrefix, date);
        return redis.getTopN(key, limit);
    }

    public Double getScore(String periodPrefix, LocalDate date, Long productId) {
        String key = buildKey(periodPrefix, date);
        return redis.getScore(key, productId);
    }


    public String buildKey(String periodPrefix, LocalDate date) {
        return periodPrefix + date.format(FORMATTER);  // ex) ranking:daily:20250515
    }

}
