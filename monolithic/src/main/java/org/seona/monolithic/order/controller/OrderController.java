package org.seona.monolithic.order.controller;

import org.seona.monolithic.order.application.OrderService;
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

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    public void placeOrder(@RequestBody PlaceOrderRequest request) throws InterruptedException {
        orderService.placeOrder(request.toPlaceOrderCommand());
    }

    @PostMapping
    public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        CreateOrderResult result = orderService.createOrder(request.toCreateOrderCommand());
        return new CreateOrderResponse(result.orderId());
    }
}
