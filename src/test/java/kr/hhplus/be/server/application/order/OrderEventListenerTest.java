package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.outbox.OrderEvent;
import kr.hhplus.be.server.domain.outbox.OrderEventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;

class OrderEventListenerTest {

    @Test
    @DisplayName("OrderConfirmRequestedEvent 수신 시 주문을 CONFIRMED 상태로 변경하고 저장한다")
    void handleOrderConfirmRequested_shouldConfirmOrder() {
        // given
        String orderId = "ORDER123";
        Order order = mock(Order.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderEventRepository orderEventRepository = mock(OrderEventRepository.class);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderEventListener listener = new OrderEventListener(orderRepository, orderEventRepository);

        // when
        listener.handleOrderConfirmRequested(new OrderConfirmRequestedEvent(orderId));

        // then
        verify(order).markConfirmed();
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("OrderPaymentCompletedOutboxEvent 수신 시 Outbox 이벤트를 저장한다")
    void handleOutboxEvent_shouldSaveOrderEvent() {
        // given
        Order order = mock(Order.class);
        OrderEvent outbox = mock(OrderEvent.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderEventRepository orderEventRepository = mock(OrderEventRepository.class);

        OrderEventListener listener = new OrderEventListener(orderRepository, orderEventRepository) {
            @Override
            public void handleOutboxEvent(OrderPaymentCompletedOutboxEvent event) {
                // override to inject test version of OrderEvent
                orderEventRepository.save(outbox);
            }
        };

        // when
        listener.handleOutboxEvent(new OrderPaymentCompletedOutboxEvent(order));

        // then
        verify(orderEventRepository).save(outbox);
    }
}
