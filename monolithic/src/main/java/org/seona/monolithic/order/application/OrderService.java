package org.seona.monolithic.order.application;

import org.seona.monolithic.order.application.dto.PlaceOrderCommand;
import org.seona.monolithic.order.domain.Order;
import org.seona.monolithic.order.domain.OrderItem;
import org.seona.monolithic.order.infrastructure.OrderItemRepository;
import org.seona.monolithic.order.infrastructure.OrderRepository;
import org.seona.monolithic.point.application.PointService;
import org.seona.monolithic.product.application.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PointService pointService;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, PointService pointService, ProductService productService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.pointService = pointService;
        this.productService = productService;
    }

    // 주문 메서드
    @Transactional // 이거 누락하면 db 트랜잭션이 동작하지 않아서 주문은 정상적으로 되는데 포인트가 차감안하는 문제 발생, db 트랜잭션의 원자성 활용을 위한 트랜잭션을 필수 선언해야함.
    public void placeOrder(PlaceOrderCommand command) {
        Order order = orderRepository.save(new Order());
        Long totalPrice = 0L;

        for (PlaceOrderCommand.OrderItem item : command.orderItems()) {
            OrderItem orderItem = new OrderItem(order.getId(), item.productId(), item.quantity());
            orderItemRepository.save(orderItem);

            Long price = productService.buy(item.productId(), item.quantity());
            totalPrice += price;
        }

        pointService.use(1L, totalPrice);
    }
}
