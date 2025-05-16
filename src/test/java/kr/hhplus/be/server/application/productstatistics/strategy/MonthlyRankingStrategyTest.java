package kr.hhplus.be.server.application.productstatistics.strategy;

import kr.hhplus.be.server.infrastructure.redis.ProductRankRedisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class MonthlyRankingStrategyTest {

    @Mock
    ProductRankRedisRepository redis;

    @InjectMocks
    MonthlyRankingStrategy strategy;

    @Test
    @DisplayName("record 메서드가 호출되면 Redis에 점수를 증가시키고 만료 시간을 설정해야 한다.")
    void record_should_call_incrementScore_and_expire() {
        Long productId = 1L;
        int quantity = 10;

        String expectedKey = "ranking:monthly:" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        strategy.record(productId, quantity);

        verify(redis).incrementScore(expectedKey, productId, quantity);
        verify(redis).expireIfAbsent(expectedKey, Duration.ofDays(3));
    }
}
