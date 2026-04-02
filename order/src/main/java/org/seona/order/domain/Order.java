package org.seona.order.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public Order() {
        status = OrderStatus.CREATED;
    }

    public Long getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public enum OrderStatus {
        CREATED, COMPLETED, REQUESTED, FAILED
    }

    public void complete() {
        status = OrderStatus.COMPLETED;
    }

    public void request() {
        if (status != OrderStatus.CREATED) {
            throw new RuntimeException("잘못된 요청입니다.");
        }

        status = OrderStatus.REQUESTED;
    }

    public void fail() {
        if (status != OrderStatus.REQUESTED) {
            throw new RuntimeException("잘못된 요청입니다.");
        }

        status = OrderStatus.FAILED;
    }
}
