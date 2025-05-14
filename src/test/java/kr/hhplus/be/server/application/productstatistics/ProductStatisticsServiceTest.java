package kr.hhplus.be.server.application.productstatistics;

import kr.hhplus.be.server.application.product.PopularProductCriteria;
import kr.hhplus.be.server.domain.productstatistics.ProductStatistics;
import kr.hhplus.be.server.domain.productstatistics.ProductStatisticsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductStatisticsServiceTest {


    @Mock
    ProductStatisticsRepository repository;

    @Mock
    ProductRankingService productRankingService;

    @InjectMocks
    ProductStatisticsService service;

    @Test
    @DisplayName("상품 판매 기록 시 Redis에 저장된다")
    void record_callsRedisRepository() {
        // given
        Long productId = 1L;
        int quantity = 3;
        long unitAmount = 10000;

        // when
        service.record(new RecordSalesCommand(productId, quantity, unitAmount));

        // then
        verify(productRankingService).record(productId, quantity);
    }

    @Test
    @DisplayName("인기 상품 조회 시 성공")
    void getTopSellingProducts_success() {
        // given
        int days = 3;
        int limit = 5;
        LocalDate fixedToday = LocalDate.of(2020, 1, 4);
        Clock fixedClock = Clock.fixed(fixedToday.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

        service = new ProductStatisticsService(repository, productRankingService, fixedClock);

        PopularProductCriteria criteria = new PopularProductCriteria(days, limit);

        // when
        service.getTopSellingProducts(criteria);

        // then
        LocalDate expectedFrom = fixedToday.minusDays(days);
        verify(repository).findTopSellingProducts(expectedFrom, fixedToday, limit);
    }
}
