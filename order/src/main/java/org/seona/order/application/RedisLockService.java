package org.seona.order.application;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisLockService {
    private final StringRedisTemplate stringRedisTemplate;

    public RedisLockService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    // 락 점유용
    public boolean tryLock(String lockKey, String lockValue) {
        return this.stringRedisTemplate.opsForValue().setIfAbsent(lockKey, lockValue);
    }

    // 락 해제용
    public void releaseLock(String lockKey) {
        stringRedisTemplate.delete(lockKey);
    }
}
