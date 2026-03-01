package org.seona.monolithic.order.controller.dto;

import org.seona.monolithic.order.application.dto.PlaceOrderCommand;

import java.util.List;

public record PlaceOrderRequest(List<OrderItem> orderItems) {

    // 클라이언트에서 받은 정보를 비즈니스 로직에서 사용할 수 있도록 변환하는 메서드
    public PlaceOrderCommand toPlaceOrderCommand() {
        return new PlaceOrderCommand(orderItems.stream()
                .map(item -> new PlaceOrderCommand.OrderItem(item.productId, item.quantity))
                .toList());
    }


    public record OrderItem(Long productId, Long quantity) {}
}
