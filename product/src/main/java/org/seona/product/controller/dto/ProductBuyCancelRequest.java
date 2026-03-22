package org.seona.product.controller.dto;

import org.seona.product.application.dto.ProductBuyCancelCommand;

public record ProductBuyCancelRequest(String requestId) {

    // 서비스에서 사용하기 위한 컨버팅용 커맨드
    public ProductBuyCancelCommand toCommand() {
        return new ProductBuyCancelCommand(requestId);
    }
}
