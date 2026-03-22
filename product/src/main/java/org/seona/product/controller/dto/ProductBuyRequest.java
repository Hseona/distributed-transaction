package org.seona.product.controller.dto;

import org.seona.product.application.dto.ProductBuyCommand;

import java.util.List;

/**
 * 어떤 상품을 몇개 구매할지 알아야 하기 때문에 구매 정보 담을 클래스 추가 생성
 */
public record ProductBuyRequest(String requestId, List<ProductInfo> productInfos) {

    // 서비스에서 사용할 수 있도록 컨버팅 해주는 메서드
    public ProductBuyCommand toCommand() {
        return new ProductBuyCommand(requestId, productInfos
                .stream()
                .map(info -> new ProductBuyCommand.ProductInfo(info.productId, info.quantity))
                .toList()
        );
    }

    public record ProductInfo(Long productId, Long quantity) {

    }
}
