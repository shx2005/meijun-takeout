package com.mo.api.service;

import com.mo.entity.Cart;

public interface CartService {
    Cart getCart(Long userId);
}
