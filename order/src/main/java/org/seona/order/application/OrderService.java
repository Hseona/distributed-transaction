package org.seona.order.application;

import org.seona.order.application.dto.CreateOrderCommand;
import org.seona.order.application.dto.CreateOrderResult;
import org.seona.order.application.dto.OrderDto;
import org.seona.order.domain.Order;
import org.seona.order.domain.OrderItem;
import org.seona.order.infrastructure.OrderItemRepository;
import org.seona.order.infrastructure.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    // 주문 생성
    public CreateOrderResult createOrder(CreateOrderCommand command) {
        Order order = orderRepository.save(new Order());

        List<OrderItem> orderItems = command.items()
                .stream()
                .map(item -> orderItemRepository.save(new OrderItem(order.getId(), item.productId(), item.quantity())))
                .toList();

        orderItemRepository.saveAll(orderItems);

        return new CreateOrderResult(order.getId());
    }

    // 주문 상태 변경 메서드
    public void request(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        order.request();
        orderRepository.save(order);
    }

    // 주문을 하려면 어떤 아이템들을 살 것인지에 대한 정보를 처리하기 위한 메서드
    public OrderDto getOrder(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);

        return new OrderDto(
                orderItems.stream()
                        .map(item -> new OrderDto.OrderItem(item.getProductId(), item.getQuantity()))
                        .toList()
        );
    }

    // 주문 완료로 상태 변경하기 위한 메서드
    public void complete(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        order.complete();
        orderRepository.save(order);
    }

    public void fail(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        order.fail();
        orderRepository.save(order);
    }
}
