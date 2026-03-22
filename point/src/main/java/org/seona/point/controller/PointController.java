package org.seona.point.controller;

import org.seona.point.application.PointService;
import org.seona.point.application.RedisLockService;
import org.seona.point.controller.dto.PointUseCancelRequest;
import org.seona.point.controller.dto.PointUseRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/point")
public class PointController {
    private final PointService pointService;
    private final RedisLockService redisLockService;

    public PointController(PointService pointService, RedisLockService redisLockService) {
        this.pointService = pointService;
        this.redisLockService = redisLockService;
    }

    @PostMapping("/use")
    public void use(@RequestBody PointUseRequest request) {
        String lockKey = "point:orchestration:" + request.requestId();

        boolean lockAcquired = redisLockService.tryLock(lockKey, request.requestId());

        if (!lockAcquired) {
            throw new RuntimeException("Failed to acquire lock for request " + request.requestId());
        }

        try {
            pointService.use(request.toCommand());
        } finally {
            redisLockService.releaseLock(lockKey);
        }
    }

    @PostMapping("/use/cancel")
    public void cancel(@RequestBody PointUseCancelRequest request) {
        String lockKey = "point:orchestration:" + request.requestId();

        boolean lockAcquired = redisLockService.tryLock(lockKey, request.requestId());

        if (!lockAcquired) {
            throw new RuntimeException("Failed to acquire lock for request " + request.requestId());
        }

        try {
            pointService.cancel(request.toCommand());
        } finally {
            redisLockService.releaseLock(lockKey);
        }
    }
}
