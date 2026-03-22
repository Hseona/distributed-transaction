package org.seona.point.init;

import jakarta.annotation.PostConstruct;
import org.seona.point.domain.Point;
import org.seona.point.infrastructure.PointRepository;
import org.springframework.stereotype.Component;

@Component
public class TestDataCreator {
    private final PointRepository pointRepository;

    public TestDataCreator(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    @PostConstruct
    public void createTestData() {
        pointRepository.save(new Point(1L, 10000L));
    }
}
