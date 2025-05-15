package com.mo.api.service;

import com.mo.api.dto.OrderPageQueryDTO;
import com.mo.common.result.PageResult;
import com.mo.entity.Order;

import java.util.List;

public interface OrderService {
    List<Order> getAll();
    List<Order> getPage(int offset, int size, String uuid);
    Order getOrderById(Long orderId);
}
