package org.seona.point.application;

import org.seona.point.application.dto.PointUseCommand;
import org.seona.point.domain.Point;
import org.seona.point.domain.PointTransactionHistory;
import org.seona.point.infrastructure.PointRepository;
import org.seona.point.infrastructure.PointTransactionHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PointService {
    private final PointRepository pointRepository;
    private final PointTransactionHistoryRepository pointTransactionHistoryRepository;

    public PointService(PointRepository pointRepository, PointTransactionHistoryRepository pointTransactionHistoryRepository) {
        this.pointRepository = pointRepository;
        this.pointTransactionHistoryRepository = pointTransactionHistoryRepository;
    }

    // 포인트 사용 메서드
    @Transactional
    public void use(PointUseCommand command) {
        PointTransactionHistory history = pointTransactionHistoryRepository.findByRequestIdAndTransactionType(command.requestId(), PointTransactionHistory.TransactionType.USE);
        if (history != null) {
            System.out.println("이미 사용한 이력이 존재합니다.");
            return;
        }

        // 이력이 없는 경우 포인트 사용하고 사용 이력 생성
        Point point = pointRepository.findByUserId(command.userId());
        if (point == null) {
            throw new RuntimeException("Point not found.");
        }

        point.use(command.amount());
        pointTransactionHistoryRepository.save(new PointTransactionHistory(command.requestId(),
                point.getId(),
                command.amount(),
                PointTransactionHistory.TransactionType.USE));
    }
}
