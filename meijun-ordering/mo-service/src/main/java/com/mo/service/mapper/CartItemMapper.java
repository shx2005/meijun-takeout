package com.mo.service.mapper;

import com.mo.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CartItemMapper {

    List<CartItem> getItemsByUserId(Long userId);

    CartItem getItemByUserIdAndItemId(Long userId, Long itemId);

    void saveCartItem(CartItem cartItem);
    void deleteCartItemById(Long id);
    int updateCartItem(CartItem cartItem);
    void deleteCartItemByUserId(Long userId);
    void deleteCartItem(CartItem item);
}
