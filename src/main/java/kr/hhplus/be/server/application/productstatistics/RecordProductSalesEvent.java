package kr.hhplus.be.server.application.productstatistics;

import java.util.List;

public record RecordProductSalesEvent(
        String orderId,
        List<ProductQuantity> items
) {
    public record ProductQuantity(long productId, int quantity) {}
}
