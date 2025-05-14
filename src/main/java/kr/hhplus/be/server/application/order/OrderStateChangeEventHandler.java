package kr.hhplus.be.server.application.order;

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

    private final OrderUseCase orderUseCase;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handle(OrderStateChangeEvent event) {
        log.info("[Order] 주문 확인 요청 이벤트 수신 - orderId={}", event.orderId());
        orderUseCase.confirmOrder(event.orderId());
    }
}
