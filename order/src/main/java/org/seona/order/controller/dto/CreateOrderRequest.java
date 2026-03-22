package org.seona.order.controller.dto;

import org.seona.order.application.dto.CreateOrderCommand;

import java.util.List;

public record CreateOrderRequest(List<OrderItem> items) {

    // service 로직에서 사용할 수 있도록 컨버터 로직 추가
    public CreateOrderCommand toCommand() {
        return new CreateOrderCommand(
                items.stream()
                        .map(item -> new CreateOrderCommand.OrderItem(item.productId, item.quantity))
                        .toList()
        );
    }

    public record OrderItem(Long productId, Long quantity) {}
}
