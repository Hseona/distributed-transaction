package org.seona.order.domain;

import jakarta.persistence.*;

// 오류 발생 시 데이터 기록용 entity
@Entity
@Table(name = "compensation_registries")
public class CompensationRegistry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    @Enumerated(EnumType.STRING)
    private CompensationRegistryStatus status;

    public CompensationRegistry() {

    }

    public CompensationRegistry(Long orderId) {
        this.orderId = orderId;
        this.status = CompensationRegistryStatus.PENDING;
    }

    public enum CompensationRegistryStatus {
        PENDING, COMPLETED
    }
}
