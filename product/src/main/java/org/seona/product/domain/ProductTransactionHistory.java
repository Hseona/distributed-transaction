package org.seona.product.domain;

import jakarta.persistence.*;

/**
 * 로직의 멱등성을 보장하고 실패시 보상 트랜잭션을 정확히 처리할 수 있도록 재고가 차감되거나 롤백될 때마다 이력을 기록하기 위한 엔티티
 * 각 요청에 대한 처리 내역을 저장하며 요청이 이미 처리된 것인지 여부를 판단하여 중복 처리를 방지하는 역할,
 * 보상 트랜잭션이 발생할 경우 기존 요청 내역을 바탕으로 롤백 처리 수행
 */
@Entity
@Table(name = "product_transaction_histories")
public class ProductTransactionHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requestId;
    private Long productId;
    private Long quantity;
    private Long price;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    public enum TransactionType {
        PURCHASE, CANCEL
    }

    public ProductTransactionHistory() {
    }

    public ProductTransactionHistory(String requestId, Long productId, Long quantity, Long price, TransactionType transactionType) {
        this.requestId = requestId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.transactionType = transactionType;
    }

    public Long getPrice() {
        return price;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public String getRequestId() {
        return requestId;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getQuantity() {
        return quantity;
    }
}
