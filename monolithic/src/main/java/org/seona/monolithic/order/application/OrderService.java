package org.seona.monolithic.order.application;

import org.seona.monolithic.order.application.dto.CreateOrderCommand;
import org.seona.monolithic.order.application.dto.CreateOrderResult;
import org.seona.monolithic.order.application.dto.PlaceOrderCommand;
import org.seona.monolithic.order.domain.Order;
import org.seona.monolithic.order.domain.OrderItem;
import org.seona.monolithic.order.infrastructure.OrderItemRepository;
import org.seona.monolithic.order.infrastructure.OrderRepository;
import org.seona.monolithic.point.application.PointService;
import org.seona.monolithic.product.application.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional
    public CreateOrderResult createOrder(CreateOrderCommand command) {
        Order order = orderRepository.save(new Order());
        List<OrderItem> orderItems = command.orderItems()
                .stream()
                .map(item -> new OrderItem(order.getId(), item.productId(), item.quantity()))
                .toList();

        orderItemRepository.saveAll(orderItems);
        return new CreateOrderResult(order.getId());
    }

    // 주문 메서드
    // tobe: 요청받는 주문id를 기준으로 재고를 차감하고 포인트를 사용하도록 변경
    @Transactional // 이거 누락하면 db 트랜잭션이 동작하지 않아서 주문은 정상적으로 되는데 포인트가 차감안하는 문제 발생, db 트랜잭션의 원자성 활용을 위한 트랜잭션을 필수 선언해야함.
    public void placeOrder(PlaceOrderCommand command) throws InterruptedException {
        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == Order.OrderStatus.COMPLETED) {
            return;
        }

        Long totalPrice = 0L;
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());

        for (OrderItem item : orderItems) {
            Long price = productService.buy(item.getProductId(), item.getQuantity());
            totalPrice += price;
        }

        pointService.use(1L, totalPrice);
        order.complete();

        System.out.println("결제 완료");
        Thread.sleep(3000);
    }
}
