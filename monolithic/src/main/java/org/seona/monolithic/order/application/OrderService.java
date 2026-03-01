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

        // 이미 주문된 주문일 경우 수행하지 않고 바로 리턴 처리함
        if (order.getStatus() == Order.OrderStatus.COMPLETED) {
            return;
        }

        Long totalPrice = 0L;
        // 완료되지 않은 경우 orderId를 이용해서 주문상품정보를 가져옴
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());

        // 가져온 주문상품 정보 기반으로 재고 차감
        for (OrderItem item : orderItems) {
            Long price = productService.buy(item.getProductId(), item.getQuantity());
            totalPrice += price;
        }

        pointService.use(1L, totalPrice);

        order.complete();
        orderRepository.save(order);

        System.out.println("결제 완료");
        Thread.sleep(3000);
    }
}
