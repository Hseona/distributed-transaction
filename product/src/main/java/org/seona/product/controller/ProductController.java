package org.seona.product.controller;

import org.seona.product.application.ProductService;
import org.seona.product.application.RedisLockService;
import org.seona.product.application.dto.ProductBuyCancelCommand;
import org.seona.product.application.dto.ProductBuyResult;
import org.seona.product.controller.dto.ProductBuyCancelRequest;
import org.seona.product.controller.dto.ProductBuyCancelResponse;
import org.seona.product.controller.dto.ProductBuyRequest;
import org.seona.product.controller.dto.ProductBuyResponse;
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
    public ProductBuyResponse buy(@RequestBody ProductBuyRequest request) {
        String lockKey = "product:orchestration:" + request.requestId();

        boolean lockAcquired = redisLockService.tryLock(lockKey, request.requestId());
        if (!lockAcquired) {
            System.out.println("락 획득에 실패했습니다.");
            throw new RuntimeException("락 획득에 실패했습니다.");
        }

        try {
            return new ProductBuyResponse(productService.buy(request.toCommand()).totalPrice());
        } finally {
            redisLockService.releaseLock(lockKey);
        }
    }

    @PostMapping("/buy/cancel")
    public ProductBuyCancelResponse cancel(@RequestBody ProductBuyCancelRequest request) {
        String lockKey = "product:orchestration:" + request.requestId();

        boolean lockAcquired = redisLockService.tryLock(lockKey, request.requestId());
        if (!lockAcquired) {
            System.out.println("락 획득에 실패했습니다.");
            throw new RuntimeException("락 획득에 실패했습니다.");
        }

        try {
            return new ProductBuyCancelResponse(productService.cancel(request.toCommand()).totalPrice());
        } finally {
            redisLockService.releaseLock(lockKey);
        }
    }
}
