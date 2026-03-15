package org.seona.product.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long quantity;
    private Long price;

    @Version
    private Long version;

    public Product() {}

    public Product(Long quantity, Long price) {
        this.quantity = quantity;
        this.price = price;
    }

    public Long calculatePrice(Long quantity) {
        return quantity * price;
    }

    public void buy(Long quantity) {
        if (this.quantity < quantity) {
            throw new RuntimeException("Quantity less than quantity");
        }

        this.quantity = this.quantity - quantity;
    }

    // 취소할 때 상품 재고 늘리기
    public void cancel(Long quantity) {
        this.quantity = this.quantity + quantity;
    }
}
