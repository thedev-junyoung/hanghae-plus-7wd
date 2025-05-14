package kr.hhplus.be.server.application.productstatistics;

import kr.hhplus.be.server.application.productstatistics.strategy.ProductRankingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductRankingService {

    private final List<ProductRankingStrategy> strategies;

    public void record(Long productId, int quantity) {
        for (ProductRankingStrategy strategy : strategies) {
            strategy.record(productId, quantity);
        }
    }
}
