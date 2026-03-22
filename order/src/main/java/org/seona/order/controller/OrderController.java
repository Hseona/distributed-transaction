package org.seona.order.controller;

import org.seona.order.application.OrderService;
import org.seona.order.controller.dto.CreateOrderRequest;
import org.seona.order.controller.dto.CreateOrderResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        return new CreateOrderResponse(orderService.createOrder(request.toCommand()).orderId());
    }
}
