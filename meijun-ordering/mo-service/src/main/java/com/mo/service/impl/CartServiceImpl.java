// Test comment to check edit functionality
package com.mo.service.impl;

import com.mo.api.service.CartService;
import com.mo.entity.Cart;
import com.mo.entity.CartItem;
import com.mo.entity.Dish;
import com.mo.common.enumeration.ItemType;
import com.mo.service.annotation.AutoFillTime;
import com.mo.service.mapper.CartItemMapper;
import com.mo.service.mapper.CartMapper;
import com.mo.service.mapper.DishMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.mo.common.constant.RedisKeyConstant;
import com.mo.common.context.BaseContext;

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

    @Autowired
    private DishMapper dishMapper;

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

        // Get or create cart for the user
        Cart cart = cartMapper.getCartByUserId(userId);
        Long cartIdToUse;
        if (cart == null) {
            Cart newCart = Cart.builder().userId(userId).build();
            // Assuming createTime/updateTime are auto-filled by @AutoFillTime on saveCart or DB defaults
            cartMapper.saveCart(newCart); // saveCart now correctly sets the ID back to newCart object
            cartIdToUse = newCart.getId();
        } else {
           cartIdToUse = cart.getId();
        }

        // Crucial: Set the determined cartId on the cartItem
        cartItem.setCartId(cartIdToUse);

        // Check if item already exists in this cart
        // getItemByUserIdAndItemId was modified to JOIN carts and use c.user_id. This should be fine.
        // However, a more direct query getItemByCartIdAndItemId would be cleaner if userId is not needed further here.
        // For now, existing query should work as cartItem.getUserId() is still valid.
        CartItem existingCartItem = cartItemMapper.getItemByUserIdAndItemId(userId, itemId); // This query now uses JOIN

        if(existingCartItem != null && existingCartItem.getItemType() == cartItem.getItemType()){
            // Item exists, update quantity.
            // existingCartItem already has its own id (cart_items.id) and cartId from the DB.
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItem.getQuantity());
            // updateCartItem uses userId and itemId in its WHERE clause via JOIN.
            cartItemMapper.updateCartItem(existingCartItem);
        } else {
            // Item does not exist in cart, populate name and price then save new cart item.
            // cartItem now has its cartId field populated.

            // Populate name and price for new items
            if (cartItem.getItemType() == ItemType.dish) {
                Dish dish = dishMapper.getDishById(itemId);
                if (dish != null) {
                    cartItem.setName(dish.getName());
                    cartItem.setUnitPrice(dish.getPrice()); // Dish.price to CartItem.unitPrice
                } else {
                    log.error("Attempted to add non-existent dish with id {} to cart for user {}", itemId, userId);
                    throw new RuntimeException("Dish not found with id: " + itemId + " when adding to cart.");
                }
            } else if (cartItem.getItemType() == ItemType.set_meal) {
                // TODO: Implement similar lookup for setmeals if their name/price are not in DTO
                // For now, assuming DTO for setmeal might contain name/price or they are handled differently.
                log.warn("Adding a SETMEAL (id: {}) to cart. Name/Price lookup not implemented. Ensure DTO provides them if required.", itemId);
                // If setmeal name/price are required and not on cartItem, this will lead to null column error later.
            }

            // Final null checks before saving to prevent SQL errors
            if (cartItem.getName() == null && cartItem.getItemType() == ItemType.dish) {
                 log.error("CartItem name is null before saving for DISH itemId {}. This should have been populated.", itemId);
                 throw new IllegalStateException("CartItem name cannot be null for a DISH when adding to cart.");
            }
            // Price for DISH is also critical. For SETMEAL, it might be calculated or handled differently.
            if (cartItem.getUnitPrice() == null && cartItem.getItemType() == ItemType.dish) {
                 log.error("CartItem unitPrice is null before saving for DISH itemId {}. This should have been populated.", itemId);
                 throw new IllegalStateException("CartItem unitPrice cannot be null for a DISH when adding to cart.");
            }

            cartItemMapper.saveCartItem(cartItem); // saveCartItem now uses #{cartId}
        }
    }

    @Override
    public void updateCartItem(CartItem cartItemFromDTO){
        String uuid = BaseContext.getCurrentId();
        if (uuid == null) {
            log.error("Cannot update cart item, uuid not found in BaseContext for cart update.");
            // Consider throwing an exception like UserNotLoginException or a custom CartUpdateException
            // For now, just logging and returning to prevent NullPointerException on redisService call.
            return;
        }

        Object userIdObj = redisTemplate.opsForHash().get(RedisKeyConstant.USER_ID, uuid);
        if (userIdObj == null) {
            log.error("Cannot update cart item, userId not found in Redis for uuid: {} for cart update.", uuid);
            // Consider throwing an exception
            return;
        }

        Long currentUserId;
        try {
            currentUserId = ((Number) userIdObj).longValue();
        } catch (ClassCastException e) {
            log.error("Failed to cast userId from Redis to Long for cart update. uuid: {}, Value from Redis: {}.", uuid, userIdObj, e);
            // Consider throwing an exception
            return;
        }

        // Prepare the CartItem object for the mapper, ensuring it has the correct userId and the new quantity from the DTO.
        CartItem itemToUpdate = new CartItem();
        itemToUpdate.setUserId(currentUserId);                     // Set the actual user ID from context
        itemToUpdate.setItemId(cartItemFromDTO.getItemId());       // Set itemId from DTO
        itemToUpdate.setQuantity(cartItemFromDTO.getQuantity());   // Set new quantity from DTO
        // If your CartItemMapper's updateCartItem SQL's WHERE clause also considers itemType for uniqueness,
        // you should also set it here from cartItemFromDTO:
        // itemToUpdate.setItemType(cartItemFromDTO.getItemType());

        int rowsAffected = cartItemMapper.updateCartItem(itemToUpdate);

        if (rowsAffected > 0) {
            log.info("Successfully updated cart item for userId: {}, itemId: {}, new quantity: {}. Rows affected: {}",
                    currentUserId, itemToUpdate.getItemId(), itemToUpdate.getQuantity(), rowsAffected);
        } else {
            log.warn("Update cart item for userId: {}, itemId: {}, new quantity: {} did not affect any rows. " +
                    "Item might not exist in this user's cart, or the quantity was already the same.",
                    currentUserId, itemToUpdate.getItemId(), itemToUpdate.getQuantity());
            // Depending on business logic, you might want to throw an exception if item not found and update is expected to succeed only for existing items.
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
