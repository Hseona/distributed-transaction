package org.seona.product.controller;

import org.seona.product.application.ProductService;
import org.seona.product.application.RedisLockService;
import org.seona.product.application.dto.ProductBuyResult;
import org.seona.product.controller.dto.ProductBuyRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final StringRedisTemplate stringRedisTemplate;
    private final ProductService productService;
    private final RedisLockService redisLockService;

    public ProductController(StringRedisTemplate stringRedisTemplate, ProductService productService, RedisLockService redisLockService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.productService = productService;
        this.redisLockService = redisLockService;
    }

    @PostMapping("/buy")
    public ProductBuyResult buy(@RequestBody ProductBuyRequest request) {
        String lockKey = "product:orchestration:" + request.requestId();

        boolean lockAcquired = redisLockService.tryLock(lockKey, request.requestId());
        if (!lockAcquired) {
            System.out.println("락 획득에 실패했습니다.");
            throw new RuntimeException("락 획득에 실패했습니다.");
        }

        try {
            return productService.buy(request.toCommand());
        } finally {
            redisLockService.releaseLock(lockKey);
        }
    }
}
