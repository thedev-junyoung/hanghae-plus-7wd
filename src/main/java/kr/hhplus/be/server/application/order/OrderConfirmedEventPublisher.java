package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.productstatistics.RecordProductSalesEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderConfirmedEventPublisher {

    private final ApplicationEventPublisher publisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishRankingEvent(OrderConfirmRequestedEvent event) {
        log.info("[Product] 상품 판매 기록 이벤트 발행 - orderId={}", event.orderId());
        publisher.publishEvent(new RecordProductSalesEvent(event.orderId()));
        log.info("[Product] 상품 판매 기록 이벤트 발행 완료 - orderId={}", event.orderId());
    }
}