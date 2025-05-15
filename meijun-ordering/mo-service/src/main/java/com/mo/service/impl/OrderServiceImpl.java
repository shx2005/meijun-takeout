package com.mo.service.impl;

import com.mo.api.dto.OrderPageQueryDTO;
import com.mo.api.service.OrderService;
import com.mo.common.result.PageResult;
import com.mo.entity.Order;
import com.mo.service.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public List<Order> getAll() {
        return orderMapper.getAll();
    }

    @Override
    public List<Order> getPage(int offset, int size, String uuid){
        return orderMapper.getPage(offset,size,uuid);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderMapper.getOrderById(orderId);
    }
}
