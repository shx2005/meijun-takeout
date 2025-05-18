package com.mo.web.controller;

import com.mo.api.service.CartService;
import com.mo.common.result.Result;
import com.mo.entity.Cart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final RedisTemplate<Object, Object> redisTemplate;
    private final CartService cartService;

    public CartController(RedisTemplate<Object, Object> redisTemplate, CartService cartService) {
        this.redisTemplate = redisTemplate;
        this.cartService = cartService;
    }

    @GetMapping("")
    public Result<Cart> getCart(){
        Long userId = (Long) redisTemplate.opsForValue().get("userId");
        Cart cart = cartService.getCart(userId);

        return Result.success(cart);
    }
}
