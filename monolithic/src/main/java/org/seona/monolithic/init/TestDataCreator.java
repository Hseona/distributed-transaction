package org.seona.monolithic.init;

import jakarta.annotation.PostConstruct;
import org.seona.monolithic.point.domain.Point;
import org.seona.monolithic.point.repository.PointRepository;
import org.seona.monolithic.product.domain.Product;
import org.seona.monolithic.product.infrastructure.ProductRepository;
import org.springframework.stereotype.Component;

@Component
public class TestDataCreator {
    private final PointRepository pointRepository;
    private final ProductRepository productRepository;

    public TestDataCreator(PointRepository pointRepository, ProductRepository productRepository) {
        this.pointRepository = pointRepository;
        this.productRepository = productRepository;
    }

    @PostConstruct
    public void createTestData() {
        pointRepository.save(new Point(1L, 10000000L));

        Product product1 = new Product(100L, 100L);
        Product product2 = new Product(100L, 200L);

        productRepository.save(product1);
        productRepository.save(product2);
    }
}
