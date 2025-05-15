package kr.hhplus.be.server.application.productstatistics;

import kr.hhplus.be.server.application.product.PopularProductCriteria;
import kr.hhplus.be.server.common.vo.Money;
import kr.hhplus.be.server.domain.productstatistics.ProductStatistics;
import kr.hhplus.be.server.domain.productstatistics.ProductStatisticsId;
import kr.hhplus.be.server.domain.productstatistics.ProductStatisticsRepository;
import kr.hhplus.be.server.infrastructure.redis.ProductRankRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductStatisticsServiceIntegrationTest {

    @Autowired
    ProductStatisticsService service;

    @Autowired
    ProductStatisticsRepository repository;

    @Autowired
    ProductRankRedisRepository redisRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final Long productId1 = 1L;
    private final Long productId2 = 2L;
    private final int delta1 = 3;
    private final int delta2 = 5;
    private LocalDate targetDate;
    private String redisKey;

    @BeforeEach
    void setUp() {
        targetDate = LocalDate.now().minusDays(1); // 어제
        redisKey = "ranking:daily:" + targetDate.format(FORMATTER);

        redisRepository.incrementScore(redisKey, productId1, delta1);
        redisRepository.incrementScore(redisKey, productId2, delta2);
        redisRepository.incrementScore(redisKey, 9999L, 0); // 무효 점수
    }

    @Test
    @DisplayName("최근 3일간의 통계 기반으로 인기 상품 정렬 결과가 유효하다")
    void getTopSellingProducts_basedOnActualData() {
        // given
        PopularProductCriteria criteria = new PopularProductCriteria(3, 5);

        // when
        Collection<ProductSalesInfo> results = service.getTopSellingProducts(criteria);
        List<ProductSalesInfo> resultList = new ArrayList<>(results);

        // then
        assertThat(resultList).isNotEmpty();
        assertThat(resultList.size()).isLessThanOrEqualTo(5);

        // 정렬 검증
        for (int i = 1; i < resultList.size(); i++) {
            assertThat(resultList.get(i - 1).salesCount())
                    .isGreaterThanOrEqualTo(resultList.get(i).salesCount());
        }

        // 기본 출력 확인
        resultList.forEach(result -> {
            System.out.printf("상품 ID: %d, 판매량: %d\n", result.productId(), result.salesCount());
            assertThat(result.salesCount()).isGreaterThan(0);
        });
    }


    @Test
    @DisplayName("syncDailyStatistics - Redis → DB 통계 저장 검증")
    void syncDailyStatistics_should_write_to_database() {
        // when
        service.syncDailyStatistics(targetDate);

        // then
        Optional<ProductStatistics> stat1 = repository.findByProductIdAndStatDate(productId1, targetDate);
        Optional<ProductStatistics> stat2 = repository.findByProductIdAndStatDate(productId2, targetDate);
        Optional<ProductStatistics> none = repository.findByProductIdAndStatDate(9999L, targetDate);

        assertThat(stat1).isPresent();
        assertThat(stat1.get().getSalesCount()).isEqualTo(delta1);
        assertThat(stat1.get().getSalesAmount()).isGreaterThan(0);

        assertThat(stat2).isPresent();
        assertThat(stat2.get().getSalesCount()).isEqualTo(delta2);
        assertThat(stat2.get().getSalesAmount()).isGreaterThan(0);

        assertThat(none).isEmpty();
    }
}
