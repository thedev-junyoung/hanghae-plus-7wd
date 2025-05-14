package kr.hhplus.be.server.application.productstatistics;

import kr.hhplus.be.server.application.product.PopularProductCriteria;
import kr.hhplus.be.server.common.vo.Money;
import kr.hhplus.be.server.domain.productstatistics.ProductStatistics;
import kr.hhplus.be.server.domain.productstatistics.ProductStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductStatisticsService implements ProductStatisticsUseCase {

    private final ProductStatisticsRepository repository;
    private final ProductRankingService productRankingService;
    private final Clock clock; // 추가

    @Override
    public void record(RecordSalesCommand command) {
        // Redis만 기록
        productRankingService.recordRanking(command.productId(), command.quantity());
    }

    @Override
    public List<ProductSalesInfo> getTopSellingProducts(PopularProductCriteria criteria) {
        LocalDate today = LocalDate.now(clock); // 고정 가능한 now()
        LocalDate from = today.minusDays(criteria.days());
        int limit = criteria.limit();

        return repository.findTopSellingProducts(from, today, limit);
    }

}
