package org.seona.order.config;

import org.seona.order.infrastructure.point.PointApiClient;
import org.seona.order.infrastructure.product.ProductApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

// ApiClient를 사용할 수 있도록 Bean으로 등록
@Configuration
public class ApiClientConfig {

    @Bean
    public ProductApiClient productApiClient() {
        return new ProductApiClient(
                RestClient.builder()
                        .baseUrl("http://localhost:8082")
                        .build()
        );
    }

    @Bean
    public PointApiClient pointApiClient() {
        return new PointApiClient(
                RestClient.builder()
                        .baseUrl("http://localhost:8081")
                        .build()
        );
    }
}
