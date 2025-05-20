package com.mo.api.service;

import com.mo.entity.AfterSale;
import com.mo.entity.Order;
import com.mo.entity.OrderComment;

import java.util.List;

public interface OrderService {

    List<Order> getAll();

    List<Order> getPage(int offset, int size, String uuid);

    Order getOrderById(Long orderId);

    OrderComment saveOrderComment(Long orderId, String content);

    void updateOrderStatus(Long orderId, Integer status);

    void saveAfterSale(AfterSale afterSale);
}
