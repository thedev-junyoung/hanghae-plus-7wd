package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.balance.BalanceHistoryRepository;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderException;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.outbox.OrderEvent;
import kr.hhplus.be.server.domain.outbox.OrderEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;


@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderRepository orderRepository;
    private final OrderEventRepository orderEventRepository;


    /**
     * 내부 상태 변경용 도메인 이벤트
     * 현재는 테스트 용이성을 위해 BEFORE_COMMIT으로 동기 처리.
     * TODO: 추후 AFTER_COMMIT + @Async로 변경하고 실패 시 보상 트랜잭션 구현 필요
     * - 메시지 큐 도입 고려
     * - 실패 시 재시도 로직 추가
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleOrderConfirmRequested(OrderConfirmRequestedEvent event) {
        log.info("[OrderEventListener] 주문 확인 요청 이벤트 수신 - {}", event);

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderException.NotFoundException(event.orderId()));

        order.markConfirmed();
        orderRepository.save(order);

        log.info("[OrderEventListener] 주문 상태 변경 완료 - orderId={}, status={}", order.getId(), order.getStatus());
    }

    /**
     * 외부 전송용 Outbox 이벤트
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOutboxEvent(OrderPaymentCompletedOutboxEvent event) {
        Order order = event.order();
        OrderEvent outbox = OrderEvent.paymentCompleted(order);

        orderEventRepository.save(outbox);

        log.info("[OrderEventListener] Outbox 저장 완료: orderId={}, eventId={}", order.getId(), outbox.getId());
    }
}
