package org.seona.monolithic.product.application;

import org.seona.monolithic.product.domain.Product;
import org.seona.monolithic.product.infrastructure.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Long buy(Long productId, Long quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        Long totalPrice = product.calculatePrice();
        product.buy(quantity);

        productRepository.save(product);

        return totalPrice;
    }
}
