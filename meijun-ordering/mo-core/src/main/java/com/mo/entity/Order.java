package com.mo.entity;

import com.mo.common.enumeration.OrderStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String userId;          // 用户ID
    private String merchantId;      // 商家ID
    private LocalDateTime orderTime;// 下单时间
    private OrderStatus status;     // 状态（待支付、已支付、进行中、已完成、已取消）
    private BigDecimal totalAmount; // 总金额
    private List<OrderItem> items;  // 订单项列表
    // 地址、配送方式等其他字段
}
