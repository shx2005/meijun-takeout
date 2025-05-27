package com.mo.service.impl;

import com.mo.api.service.CartService;
import com.mo.entity.Cart;
import com.mo.entity.CartItem;
import com.mo.service.annotation.AutoFillTime;
import com.mo.service.mapper.CartItemMapper;
import com.mo.service.mapper.CartMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private CartItemMapper cartItemMapper;

    @Override
    public List<CartItem> getCart(Long userId){
        return cartItemMapper.getItemsByUserId(userId);
    }

    @Override
    @AutoFillTime
    public void saveCart(Cart cart){
        cartMapper.saveCart(cart);
    }

    @Override
    public void addToCart(CartItem cartItem){
        Long userId = cartItem.getUserId();
        Long itemId = cartItem.getItemId();
        cartMapper.createCartIfNotExists(userId);

        CartItem cartItem1 = cartItemMapper.getItemByUserIdAndItemId(userId, itemId);
        if(cartItem1 != null && cartItem1.getItemType() == cartItem.getItemType()){
            cartItem1.setQuantity(cartItem1.getQuantity() + cartItem.getQuantity());
            cartItemMapper.updateCartItem(cartItem1);
        } else {
            cartItemMapper.saveCartItem(cartItem);
        }
    }

    @Override
    public void updateCartItem(CartItem cartItem){
        CartItem cartItem1 = cartItemMapper.getItemByUserIdAndItemId(cartItem.getUserId(), cartItem.getItemId());
        if(cartItem1 != null && cartItem1.getItemType() == cartItem.getItemType()){
            cartItemMapper.updateCartItem(cartItem1);
        }
    }

    @Override
    public void deleteCart(Long cartId){
        cartItemMapper.deleteCartItemById(cartId);
    }

    @Override
    public void deleteCartByUserId(Long userId){
        cartMapper.deleteCartByUserId(userId);
    }
}
