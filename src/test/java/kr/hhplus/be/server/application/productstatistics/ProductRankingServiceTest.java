package kr.hhplus.be.server.application.productstatistics;

import kr.hhplus.be.server.application.productstatistics.strategy.ProductRankingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductRankingServiceTest {


    @InjectMocks
    ProductRankingService rankingService;


    @Mock
    ProductRankingStrategy strategy1;

    @BeforeEach
    void setUp() {
        rankingService = new ProductRankingService(List.of(strategy1)); // 명시적 주입
    }

    @Test
    @DisplayName("상품 판매 수량만큼 점수 증가")
    void recordRanking_shouldIncreaseScore() {
        rankingService.record(1L, 3);

        verify(strategy1).record(1L, 3); // 단일 전략이 호출됐는지 검증
    }
}
