package com.mo.service.impl;

import com.mo.api.service.OrderService;
import com.mo.api.service.PaymentService;
import com.mo.api.vo.OrderSubmitVO;
import com.mo.common.constant.MessageConstant;
import com.mo.common.enumeration.OrderPayStaus;
import com.mo.common.enumeration.OrderStatus;
import com.mo.common.exception.CartBusinessException;
import com.mo.common.exception.OrderBusinessException;
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
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public List<Order> getAll() {
        return orderMapper.getAll();
    }

    @Override
    public List<Order> getPage(int offset, int size) {
        List<Order> orders = orderMapper.getPage(offset,size);

        for(var order: orders){
            Long id = order.getId();
            List<OrderDetail> details = orderDetailMapper.getDetailsByOrderId(id);
            order.setItems(details);
        }

        return orders;
    }

    @Override
    public List<Order> getPage(int offset, int size, Long userId){
        List<Order> orders = orderMapper.getPage(offset,size,userId);

        for(var order: orders){
            Long id = order.getId();
            List<OrderDetail> details = orderDetailMapper.getDetailsByOrderId(id);
            order.setItems(details);
        }

        return orders;
    }

    @Override
    public List<Order> getOrderByUserId(Long userId) {
        return orderMapper.getOrderByUserId(userId);
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
    public void updateOrder(Order order){
        orderMapper.updateOrder(order);
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
        BigDecimal total = cartItems.stream()
                        .map(item -> {
                            BigDecimal price = item.getPrice();
                            int quantity = item.getQuantity();
                            BigDecimal tot = price.multiply(new BigDecimal(quantity));
                            item.setTotal(tot);
                            return tot;
                        })
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

    @Override
    public void refund(Long orderId){
        Order order = orderMapper.getOrderById(orderId);
        Long userId = order.getUserId();
        BigDecimal total = order.getTotal();

        Customer customer = customerMapper.getCustomerById(userId);
        customer.setBalance(customer.getBalance().add(total));
        customerMapper.updateCustomer(customer);
    }

    @Override
    public void cancelOrder(Long orderId){
        Order order = orderMapper.getOrderById(orderId);

        if(order == null)
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);

        if(order.getStatus().equals(OrderStatus.delivering))
            throw new OrderBusinessException(MessageConstant.UNABLE_TO_CANCEL_ORDER_REASON1);
        if(order.getStatus().equals(OrderStatus.completed))
            throw new OrderBusinessException(MessageConstant.UNABLE_TO_CANCEL_ORDER_REASON2);
        if(order.getStatus().equals(OrderStatus.cancelled))
            throw new OrderBusinessException(MessageConstant.UNABLE_TO_CANCEL_ORDER_REASON3);

        Order newOrder = Order.builder()
                .id(orderId)
                .status(OrderStatus.cancelled)
                .build();

        if(order.getStatus().equals(OrderStatus.unconfirmed) || order.getStatus().equals(OrderStatus.pending)){
            refund(orderId);
            newOrder.setPayStatus(OrderPayStaus.refund);
        }

        orderMapper.updateOrder(newOrder);
    }
}
