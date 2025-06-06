package com.mo.api.service;

import com.mo.api.vo.OrderSubmitVO;
import com.mo.entity.AfterSale;
import com.mo.entity.Order;
import com.mo.entity.OrderComment;

import java.util.List;

public interface OrderService {

    List<Order> getAll();

    List<Order> getPage(int offset, int size, Long userId);

    Order getOrderById(Long orderId);

    OrderComment saveOrderComment(Long orderId, String content);

    void updateOrder(Order order);

    void saveAfterSale(AfterSale afterSale);

    OrderSubmitVO saveOrder(Order order);
}
