package com.mo.api.service;

import com.mo.entity.Cart;
import com.mo.entity.CartItem;

import java.util.List;

public interface CartService {
    List<CartItem> getCart(Long userId);

    void saveCart(Cart cart);

    void addToCart(CartItem cartItem);

    void updateCartItem(CartItem cartItem);

    void deleteCart(Long cartId);
}
