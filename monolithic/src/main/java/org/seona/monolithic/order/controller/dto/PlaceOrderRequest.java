package org.seona.monolithic.order.controller.dto;

import org.seona.monolithic.order.application.dto.PlaceOrderCommand;

public record PlaceOrderRequest(Long orderId) {

    public PlaceOrderCommand toPlaceOrderCommand() {
        return new PlaceOrderCommand(orderId);
    }
}
