package com.mo.service.impl;

import com.mo.api.service.CartService;
import com.mo.entity.Cart;
import com.mo.service.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private CartMapper cartMapper;

    @Override
    public Cart getCart(Long userId){
        String key = "cart:" + userId;
        Cart cart = (Cart) redisTemplate.opsForValue().get(key);

        if(cart == null) cart = cartMapper.getCart(userId);
        redisTemplate.opsForValue().set(key, cart);

        return cart;
    }
}
