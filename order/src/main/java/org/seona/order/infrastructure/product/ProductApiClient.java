package org.seona.order.infrastructure.product;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.RestClient;

public class ProductApiClient {
    private final RestClient restClient;

    public ProductApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * 상품 구매 중 오류 발생 시 처리할 수 있는 재시도 로직
     * 재시도 3번, 재시도 간의 딜레이를 500으로 설정
     */
    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 500)
    )
    public ProductBuyApiResponse buy(ProductBuyApiRequest request) {
        return restClient
                .post()
                .uri("/product/buy")
                .body(request)
                .retrieve()
                .body(ProductBuyApiResponse.class);
    }

    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 500)
    )
    public ProductBuyCancelApiResponse cancel(ProductBuyCancelApiRequest request) {
        return restClient
                .post()
                .uri("/product/buy/cancel")
                .body(request)
                .retrieve()
                .body(ProductBuyCancelApiResponse.class);
    }
}
