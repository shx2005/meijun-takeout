package com.mo.service.mapper;

import com.mo.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    List<Order> getAll();
    List<Order> getPage(@Param("offset") int offset, @Param("size") int size, @Param("uuid") String uuid);

    Order getOrderById(@Param("orderId") Long orderId);

    void updateOrderStatus(@Param("orderId") Long orderId, @Param("status") Integer status);
}
