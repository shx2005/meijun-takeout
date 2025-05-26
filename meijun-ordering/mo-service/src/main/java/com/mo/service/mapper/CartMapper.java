package com.mo.service.mapper;

import com.mo.entity.Cart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CartMapper {
     void saveCart(Cart cart);
     void createCartIfNotExists(Long userId);

    void deleteCartByUserId(Long userId);
}
