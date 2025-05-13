package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.Order;

/**
 * 주문 상태가 확정된 이후, 외부 시스템 전송을 위해 Outbox 저장을 트리거하는 이벤트
 *
 * {@link OrderEventListener#handleOutboxEvent(OrderPaymentCompletedOutboxEvent)} 에서 수신되어 {@link OrderEvent} 로 저장됩니다.
 */
public record OrderPaymentCompletedOutboxEvent(Order order) {}
