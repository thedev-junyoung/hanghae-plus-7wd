package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.productstatistics.RecordProductSalesEvent;
import kr.hhplus.be.server.common.vo.Money;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderException;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.tracing.OpenTelemetryTracingAutoConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements OrderUseCase {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Order createOrder(Long userId, List<OrderItem> items, Money totalAmount) {
        Order order = Order.create(userId, items, totalAmount);
        orderRepository.save(order);
        return order;
    }
    @Transactional(readOnly = true)
    public Order getOrderForPayment(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException.NotFoundException(orderId));
        order.validatePayable();
        return order;
    }

    @Transactional
    public void markConfirmed(Order order) {
        order.markConfirmed();
        orderRepository.save(order);
    }

    @Transactional
    public void confirmOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException.NotFoundException(orderId));

        order.markConfirmed();
        orderRepository.save(order);

        List<RecordProductSalesEvent.ProductQuantity> products = order.getItems().stream()
                .map(item -> new RecordProductSalesEvent.ProductQuantity(item.getProductId(), item.getQuantity()))
                .toList();

        eventPublisher.publishEvent(new RecordProductSalesEvent(order.getId(), products));

        log.info("[Order] 주문 확인 완료 - orderId={}", orderId);
    }

}

