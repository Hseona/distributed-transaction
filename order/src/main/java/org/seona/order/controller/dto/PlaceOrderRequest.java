package org.seona.order.controller.dto;

import org.seona.order.application.dto.PlaceOrderCommand;

public record PlaceOrderRequest(Long orderId) {
    public PlaceOrderCommand toCommand() {
        return new PlaceOrderCommand(orderId);
    }
}
