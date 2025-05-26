package com.mo.service.mapper;

import com.mo.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper {
    void saveOrderDetail(OrderDetail orderDetail);
}
