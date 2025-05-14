package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderException;
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
public class OrderStateChangeEventHandler {
    private final OrderRepository orderRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handle(OrderConfirmRequestedEvent event) {
        log.info("[Order] 주문 확인 요청 이벤트 수신 - orderId={}", event.orderId());
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderException.NotFoundException(event.orderId()));
        order.markConfirmed();
        orderRepository.save(order);
        log.info("[Order] 주문 확인 완료 - orderId={}", event.orderId());
    }
}