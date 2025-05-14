package kr.hhplus.be.server.application.productstatistics;

import kr.hhplus.be.server.infrastructure.redis.ProductRankRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductRankingService {

    private final ProductRankRedisRepository redisRepository;

    private static final String RANKING_KEY_DAILY = "ranking:daily";

    public void recordRanking(Long productId, int quantity) {
        // 상품 판매 수량만큼 점수 증가
        redisRepository.incrementScore(RANKING_KEY_DAILY, productId, quantity);
    }
}
