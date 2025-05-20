package com.mo.web.controller;

import com.mo.api.dto.CartItemDTO;
import com.mo.api.service.CartService;
import com.mo.common.constant.RedisKeyConstant;
import com.mo.common.result.Result;
import com.mo.entity.Cart;
import com.mo.entity.CartItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CartService cartService;

    public CartController(RedisTemplate<String, Object> redisTemplate, CartService cartService) {
        this.redisTemplate = redisTemplate;
        this.cartService = cartService;
    }

    /**
     * 获取购物车
     * 默认为最后创建的
     * @return Result
     */
    @GetMapping("")
    public Result<Cart> getCart(){
        Long userId = (Long) redisTemplate.opsForValue().get(RedisKeyConstant.USER_ID);
        List<CartItem> cartItems = cartService.getCart(userId);

        Cart cart = Cart.builder()
                .userId(userId)
                .items(cartItems)
                .build();

        return Result.success(cart);
    }

    @PostMapping("/add")
    public Result<String> addToCart(@RequestBody CartItemDTO cartDTO){
        CartItem cartItem = new CartItem();
        BeanUtils.copyProperties(cartDTO, cartItem);
        cartService.addToCart(cartItem);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<String> updateCartItem(@RequestBody CartItemDTO cartDTO){
        CartItem cartItem = new CartItem();
        BeanUtils.copyProperties(cartDTO, cartItem);
        cartService.updateCartItem(cartItem);
        return Result.success();
    }

    @DeleteMapping("/delete")
    public Result<String> deleteCartItem(@RequestParam Long cartItemId){
        cartService.deleteCart(cartItemId);
        return Result.success();
    }
}
