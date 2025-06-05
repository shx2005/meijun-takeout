package com.mo.service.impl;

import com.mo.api.service.OrderService;
import com.mo.api.vo.OrderSubmitVO;
import com.mo.common.constant.MessageConstant;
import com.mo.common.enumeration.OrderPayStaus;
import com.mo.common.enumeration.OrderStatus;
import com.mo.common.exception.CartBusinessException;
import com.mo.entity.*;
import com.mo.service.annotation.AutoFillTime;
import com.mo.service.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private AfterSaleMapper afterSaleMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private CartItemMapper cartItemMapper;

    @Override
    public List<Order> getAll() {
        return orderMapper.getAll();
    }

    @Override
    public List<Order> getPage(int offset, int size, Long userid){
        return orderMapper.getPage(offset,size,userid);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderMapper.getOrderById(orderId);
    }

    @Override
    @AutoFillTime
    public OrderComment saveOrderComment(Long orderId, String content){
        OrderComment orderComment = OrderComment.builder()
                .orderId(orderId)
                .content(content)
                .build();

        commentMapper.saveOrderComment(orderComment);
        return orderComment;
    }

    @Override
    @AutoFillTime
    public void updateOrderStatus(Long orderId, Integer status){
        orderMapper.updateOrderStatus(orderId,status);
    }

    @Override
    public void saveAfterSale(AfterSale afterSale){
        afterSaleMapper.saveAfterSale(afterSale);
    }

    @Override
    @AutoFillTime
    public OrderSubmitVO saveOrder(Order order){
        Long userId = order.getUserId();
        List<CartItem> cartItems = cartItemMapper.getItemsByUserId(userId);
        //  判断购物车是否为空
        if(cartItems == null || cartItems.isEmpty()){
            throw new CartBusinessException(MessageConstant.CART_NULL);
        }
        // 设置订单信息
        order.setOrderTime(LocalDateTime.now());
        order.setStatus(OrderStatus.pending);
        order.setPayStatus(OrderPayStaus.unpaid);
        order.setOrderNumber(String.valueOf(System.currentTimeMillis()));
        BigDecimal total = cartItems.stream().map(CartItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(total);

        orderMapper.saveOrder(order);

        //  保存订单详情
        List<OrderDetail> details = new ArrayList<>();
        for(CartItem item : cartItems){
            OrderDetail detail = new OrderDetail();
            BeanUtils.copyProperties(item, detail);
            detail.setOrderId(order.getId());
            details.add(detail);
            orderDetailMapper.saveOrderDetail(detail);
        }

        //  删除购物车
        cartItemMapper.deleteCartItemByUserId(userId);

        return OrderSubmitVO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderAmount(order.getTotal())
                .orderTime(order.getOrderTime())
                .build();
    }
}
