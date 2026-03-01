package org.seona.monolithic.order.controller;

import org.seona.monolithic.order.application.OrderService;
import org.seona.monolithic.order.controller.dto.PlaceOrderRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order/place")
    public void placeOrder(@RequestBody PlaceOrderRequest request) throws InterruptedException {
        orderService.placeOrder(request.toPlaceOrderCommand());

        Thread.sleep(3000);
    }
}
