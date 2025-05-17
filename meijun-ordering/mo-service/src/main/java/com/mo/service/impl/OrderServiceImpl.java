package com.mo.service.impl;

import com.mo.api.service.OrderService;
import com.mo.entity.Order;
import com.mo.entity.OrderComment;
import com.mo.service.annotation.AutoFillTime;
import com.mo.service.mapper.CommentMapper;
import com.mo.service.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AutoFillTime
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private CommentMapper commentMapper;

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

    @Override
    public OrderComment saveOrderComment(Long orderId, String content){
        OrderComment orderComment = OrderComment.builder()
                .orderId(orderId)
                .content(content)
                .build();

        commentMapper.saveOrderComment(orderComment);
        return orderComment;
    }

    @Override
    public void updateOrderStatus(Long orderId, Integer status){
        orderMapper.updateOrderStatus(orderId,status);
    }
}
