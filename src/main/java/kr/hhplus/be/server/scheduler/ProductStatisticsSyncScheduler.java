package kr.hhplus.be.server.scheduler;

import kr.hhplus.be.server.common.vo.Money;
import kr.hhplus.be.server.domain.productstatistics.ProductStatistics;
import kr.hhplus.be.server.domain.productstatistics.ProductStatisticsRepository;
import kr.hhplus.be.server.infrastructure.redis.ProductRankRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductStatisticsSyncScheduler {

    private final ProductStatisticsRepository productStatisticsRepository;
    private final ProductRankRedisRepository rankRedisRepository;

    private static final String RANKING_KEY_DAILY = "ranking:daily";

    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시
    public void syncToDatabase() {
        List<Long> top = rankRedisRepository.getTopN(RANKING_KEY_DAILY, 1000); // 또는 전체 조회
        LocalDate today = LocalDate.now();

        for (Long productId : top) {
            Double score = rankRedisRepository.getScore(RANKING_KEY_DAILY, productId);
            if (score == null || score == 0) continue;

            int quantity = score.intValue();

            ProductStatistics stats = productStatisticsRepository
                    .findByProductIdAndStatDate(productId, today)
                    .orElseGet(() -> ProductStatistics.create(productId, today));

            stats.addSales(quantity, Money.ZERO); // 금액은 생략하거나 따로 누적
            productStatisticsRepository.save(stats);
        }

        // 필요 시 Redis 랭킹 초기화도 가능
        // rankRedisRepository.clear(RANKING_KEY_DAILY);
    }
}
