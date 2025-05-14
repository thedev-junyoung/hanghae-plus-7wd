package kr.hhplus.be.server.application.productstatistics;

import kr.hhplus.be.server.infrastructure.redis.ProductRankRedisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductRankingServiceTest {

    @Mock
    ProductRankRedisRepository redisRepository;

    @InjectMocks
    ProductRankingService rankingService;

    @Test
    @DisplayName("상품 판매 수량만큼 점수 증가")
    void recordRanking_shouldIncreaseScore() {
        rankingService.recordRanking(1L, 3);
        verify(redisRepository).incrementScore("ranking:daily", 1L, 3);
    }
}
