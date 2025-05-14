package kr.hhplus.be.server.application.productstatistics;

import kr.hhplus.be.server.application.order.OrderConfirmRequestedEvent;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderException;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductRankingEventHandler {

    private final OrderRepository orderRepository;
    private final ProductRankingService rankingService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(RecordProductSalesEvent event) {
        log.info("[Product] 상품 판매 기록 이벤트 수신 - orderId={}", event.orderId());
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderException.NotFoundException(event.orderId()));
        for (OrderItem item : order.getItems()) {
            rankingService.recordRanking(item.getProductId(), item.getQuantity());
        }
        log.info("[Product] 상품 판매 기록 완료 - orderId={}", event.orderId());
    }
}
