package org.seona.point.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "points")
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long amount;

    // 동시성 문제를 방어하기 위한 버전 컬럼
    @Version
    private Long version;

    public Point() {}

    public Point(Long userId, Long amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public void use(Long amount) {
        if (this.amount < amount) {
            throw new RuntimeException("잔액이 부족합니다.");
        }

        this.amount = this.amount - amount;
    }

    // 사용 취소 시에는 포인트를 증가하도록 함
    public void cancel(Long amount) {
        this.amount = this.amount + amount;
    }

    public Long getId() {
        return id;
    }
}
