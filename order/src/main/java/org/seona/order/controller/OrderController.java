package org.seona.order.controller;

import org.seona.order.application.OrderService;
import org.seona.order.application.RedisLockService;
import org.seona.order.application.dto.OrderCoordinator;
import org.seona.order.controller.dto.CreateOrderRequest;
import org.seona.order.controller.dto.CreateOrderResponse;
import org.seona.order.controller.dto.PlaceOrderRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final RedisLockService redisLockService;
    private final OrderCoordinator orderCoordinator;

    public OrderController(OrderService orderService, RedisLockService redisLockService, OrderCoordinator orderCoordinator) {
        this.orderService = orderService;
        this.redisLockService = redisLockService;
        this.orderCoordinator = orderCoordinator;
    }

    @PostMapping
    public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        return new CreateOrderResponse(orderService.createOrder(request.toCommand()).orderId());
    }

    // 주문 요청을 받기위한 api
    @PostMapping("/place")
    public void placeOrder(@RequestBody PlaceOrderRequest request) {
        // 동일한 주문이 한번만 수행될 수 있도록 락 처리
        String lockKey = "order:" + request.orderId();
        boolean lockAcquired = redisLockService.tryLock(lockKey, request.orderId().toString());

        if (!lockAcquired) {
            throw new RuntimeException("Failed to acquire lock for order: " + request.orderId());
        }

        try {
            // 주문 진행
            orderCoordinator.placeOrder(request.toCommand());
        } finally {
            redisLockService.releaseLock(lockKey);
        }
    }
}
