package org.seona.monolithic.point.application;

import org.seona.monolithic.point.domain.Point;
import org.seona.monolithic.point.repository.PointRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PointService {
    private final PointRepository pointRepository;
    public PointService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    @Transactional
    public void use(Long userId, Long amount) {
        Point point = pointRepository.findByUserId(userId);
        if  (point == null) {
            throw new RuntimeException("Point not found");
        }

        point.use(amount);
        pointRepository.save(point);
    }
}
