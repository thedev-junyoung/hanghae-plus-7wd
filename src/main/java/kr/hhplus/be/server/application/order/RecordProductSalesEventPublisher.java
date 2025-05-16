package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.productstatistics.ProductRankingService;
import kr.hhplus.be.server.application.productstatistics.RecordProductSalesEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecordProductSalesEventPublisher {

    private final ProductRankingService rankingService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(RecordProductSalesEvent event) {
        log.info("[Product] 판매 기록 이벤트 수신 - orderId={}", event.orderId());

        for (var item : event.items()) {
            rankingService.record(item.productId(), item.quantity());
        }

        log.info("[Product] 판매 기록 완료 - orderId={}", event.orderId());
    }

}