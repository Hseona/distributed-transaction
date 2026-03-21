package org.seona.order.application;

import org.seona.order.application.dto.CreateOrderCommand;
import org.seona.order.application.dto.CreateOrderResult;
import org.seona.order.domain.Order;
import org.seona.order.domain.OrderItem;
import org.seona.order.infrastructure.OrderItemRepository;
import org.seona.order.infrastructure.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    // 주문 생성
    @Transactional
    public CreateOrderResult createOrder(CreateOrderCommand command) {
        Order order = orderRepository.save(new Order());

        List<OrderItem> orderItems = command.items()
                .stream()
                .map(item -> orderItemRepository.save(new OrderItem(order.getId(), item.productId(), item.quantity())))
                .toList();

        orderItemRepository.saveAll(orderItems);

        return new CreateOrderResult(order.getId());
    }
}
