// Test comment to check edit functionality
package com.mo.service.impl;

import com.mo.api.service.CartService;
import com.mo.common.constant.MessageConstant;
import com.mo.common.exception.CartBusinessException;
import com.mo.entity.Cart;
import com.mo.entity.CartItem;
import com.mo.entity.Dish;
import com.mo.common.enumeration.ItemType;
import com.mo.entity.SetMeal;
import com.mo.service.annotation.AutoFillTime;
import com.mo.service.mapper.CartItemMapper;
import com.mo.service.mapper.CartMapper;
import com.mo.service.mapper.DishMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private DishMapper dishMapper;

    @Override
    public List<CartItem> getCart(Long userId){
        List<CartItem> list = cartItemMapper.getItemsByUserId(userId);
        list.forEach(item -> {
            int quantity = item.getQuantity();
            BigDecimal price = item.getPrice();
            BigDecimal total = price.multiply(new BigDecimal(quantity));
            item.setTotal(total);
        });

        return list;
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
        ItemType itemType = cartItem.getItemType();

        // 获取购物车，不存在则创建
        Cart cart = cartMapper.getCartByUserId(userId);
        Long cartId;
        if (cart == null) {
            Cart newCart = Cart.builder()
                    .userId(userId)
                    .build();

            saveCart(newCart);
            cartId = newCart.getId();
        } else {
           cartId = cart.getId();
        }

        cartItem.setCartId(cartId);

        CartItem cartItem1 = cartItemMapper.getItemByUserIdAndItemId(userId, itemId, itemType);

        if(cartItem1 != null){
            int quantity = cartItem1.getQuantity() + cartItem.getQuantity();
            BigDecimal price = cartItem1.getPrice();
            cartItem1.setQuantity(quantity);
            BigDecimal total = price.multiply(new BigDecimal(quantity));
            cartItem1.setTotal(total);

            cartItemMapper.updateCartItem(cartItem1);
        } else {
            switch (itemType){
                case dish -> {
                    Dish dish = dishMapper.getDishById(itemId);
                    if(dish == null) throw new CartBusinessException(MessageConstant.DISH_NOT_FOUND);
                    cartItem.setName(dish.getName());
                    cartItem.setPrice(dish.getPrice());
                }
                case set_meal -> {
                    SetMeal setMeal = null;
                }
            }

            if (cartItem.getName() == null) {
                 log.error("购物车项 {} 名字为空", itemId);
                 throw new CartBusinessException(MessageConstant.CART_ITEM_NAME_NULL);
            }
            if (cartItem.getPrice() == null) {
                 log.error("购物车项 {} 价格为空", itemId);
                 throw new CartBusinessException(MessageConstant.CART_ITEM_PRICE_NULL);
            }

            cartItemMapper.saveCartItem(cartItem);
        }
    }

    @Override
    public void updateCartItem(CartItem cartItem) {

        int rowsAffected = cartItemMapper.updateCartItem(cartItem);

        if (rowsAffected > 0) {
            log.info("购物车项 {} 更新成功", cartItem.getId());
        } else {
            log.warn("购物车项 {} 更新失败", cartItem.getId());
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
