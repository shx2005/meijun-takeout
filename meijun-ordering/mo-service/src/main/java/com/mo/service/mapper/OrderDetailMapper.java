package com.mo.service.mapper;

import com.mo.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    void saveOrderDetail(OrderDetail orderDetail);

    List<OrderDetail> getAllOrderDetail();

    List<OrderDetail> getDetailsByOrderId(Long orderId);
}
