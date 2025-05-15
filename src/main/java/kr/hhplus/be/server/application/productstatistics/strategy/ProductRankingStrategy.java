package kr.hhplus.be.server.application.productstatistics.strategy;

public interface ProductRankingStrategy {
    void record(Long productId, int quantity);
}