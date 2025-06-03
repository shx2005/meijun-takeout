package com.mo.web.controller;

import com.mo.api.service.RedisService;
import com.mo.common.context.BaseContext;
import com.mo.common.enumeration.ItemType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.mo.api.dto.CartItemDTO;
import com.mo.api.service.CartService;
import com.mo.common.constant.RedisKeyConstant;
import com.mo.common.result.Result;
import com.mo.entity.Cart;
import com.mo.entity.CartItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "购物车管理接口", description = "购物车相关操作接口")
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;
    private final RedisService redisService;

    public CartController(CartService cartService, RedisService redisService) {
        this.cartService = cartService;
        this.redisService = redisService;
    }

    /**
     * 获取购物车
     * 默认为最后创建的
     * @return Result
     */
    @Operation(summary = "获取购物车", description = "获取用户最后创建的购物车")
    @GetMapping("")
    public Result<Cart> getCart(){
        String uuid  = BaseContext.getCurrentId();
        Object obj = redisService.hGet(RedisKeyConstant.USER_ID, uuid);
        Long userId = ((Number) obj).longValue();
        List<CartItem> cartItems = cartService.getCart(userId);

        Cart cart = Cart.builder()
                .userId(userId)
                .items(cartItems)
                .build();

        return Result.success(cart);
    }

    @Operation(summary = "添加商品到购物车", description = "将指定商品添加到用户购物车")
    @Parameters({
            @Parameter(name = "CartItem", description = "购物车商品参数", required = true, schema = @Schema(implementation = CartItem.class))
    })
    @PostMapping("/add")
    public Result<String> addToCart(@RequestBody CartItemDTO cartDTO){
        CartItem cartItem = new CartItem();
        BeanUtils.copyProperties(cartDTO, cartItem);

        // 从 BaseContext 获取当前用户的 uuid
        String uuid = BaseContext.getCurrentId();
        if (uuid == null) {
            log.error("Failed to get current user uuid from BaseContext. User might not be logged in properly or context is missing.");
            return Result.error("User not authenticated."); // 或者更合适的错误响应
        }

        // 从 Redis 获取真实的 userId (Long)
        Object userIdObj = redisService.hGet(RedisKeyConstant.USER_ID, uuid);
        if (userIdObj == null) {
            log.error("User ID not found in Redis for uuid: {}", uuid);
            return Result.error("User information not found, please login again.");
        }
        Long currentUserId;
        try {
            currentUserId = ((Number) userIdObj).longValue();
        } catch (ClassCastException e) {
            log.error("Failed to cast userId from Redis to Long for uuid: {}. Value was: {}", uuid, userIdObj, e);
            return Result.error("Invalid user information format.");
        }
        
        // 将获取到的 userId 设置到 cartItem 对象中
        cartItem.setUserId(currentUserId);

        cartService.addToCart(cartItem);
        return Result.success();
    }

    @Operation(summary = "更新购物车商品信息", description = "更新用户购物车中指定商品的信息")
    @PutMapping("/update")
    public Result<String> updateCartItem(@RequestBody CartItemDTO cartDTO){
        CartItem cartItem = new CartItem();
        BeanUtils.copyProperties(cartDTO, cartItem);
        cartService.updateCartItem(cartItem);
        return Result.success();
    }

    @Operation(summary = "从购物车删除商品", description = "从用户购物车中删除指定商品")
    @Parameters({
            @Parameter(name = "cartItemId", description = "购物车商品id", required = true, schema = @Schema(implementation = Long.class))
    })
    @DeleteMapping("/delete")
    public Result<String> deleteCartItem(@RequestParam Long cartItemId){
        cartService.deleteCart(cartItemId);
        return Result.success();
    }
}
