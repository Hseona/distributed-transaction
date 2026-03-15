package org.seona.monolithic.order.controller;

import org.seona.monolithic.order.application.OrderService;
import org.seona.monolithic.order.application.RedisLockService;
import org.seona.monolithic.order.application.dto.CreateOrderResult;
import org.seona.monolithic.order.controller.dto.CreateOrderRequest;
import org.seona.monolithic.order.controller.dto.CreateOrderResponse;
import org.seona.monolithic.order.controller.dto.PlaceOrderRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final RedisLockService redisLockService;

    public OrderController(OrderService orderService,  RedisLockService redisLockService) {
        this.orderService = orderService;
        this.redisLockService = redisLockService;
    }

    @PostMapping("/place")
    public void placeOrder(@RequestBody PlaceOrderRequest request) throws InterruptedException {
        String key = "order:monolithic:" + request.orderId();
        boolean acquiredLock = redisLockService.tryLock(key, request.orderId().toString());

        if (!acquiredLock) {
            throw new RuntimeException("락 획득에 실패했습니다.");
        }

        try {
            orderService.placeOrder(request.toPlaceOrderCommand());
        } finally {
            redisLockService.releaseLock(key);
        }
    }

    @PostMapping
    public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        CreateOrderResult result = orderService.createOrder(request.toCreateOrderCommand());
        return new CreateOrderResponse(result.orderId());
    }


}
