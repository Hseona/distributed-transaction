package org.seona.monolithic.order.application.dto;

import java.util.List;

// 주문메서드에서 사용할 파라미터
public record PlaceOrderCommand(List<OrderItem> orderItems) {

    // 어떤 상품을 몇개 주문할지에 대한 정보
    public record OrderItem(Long productId, Long quantity) {}
}
