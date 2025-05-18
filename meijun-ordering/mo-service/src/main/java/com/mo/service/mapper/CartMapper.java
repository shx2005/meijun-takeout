package com.mo.service.mapper;

import com.mo.entity.Cart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper {

    public Cart getCart(Long userId);
}
