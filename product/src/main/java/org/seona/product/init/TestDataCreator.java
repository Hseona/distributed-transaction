package org.seona.product.init;

import jakarta.annotation.PostConstruct;
import org.seona.product.domain.Product;
import org.seona.product.infrastructure.ProductRepository;
import org.springframework.stereotype.Component;

@Component
public class TestDataCreator {
    private final ProductRepository productRepository;

    public TestDataCreator(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostConstruct
    public void createTestData() {
        Product product1 = new Product(100L, 100L);
        Product product2 = new Product(100L, 200L);

        productRepository.save(product1);
        productRepository.save(product2);
    }
}
