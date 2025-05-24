package com.mo.entity;

import com.mo.common.enumeration.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "订单")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "订单ID")
    private String id;
    @Schema(description = "用户id")
    private String userId;
    @Schema(description = "商户id")
    private String merchantId;
    @Schema(description = "下单时间")
    private LocalDateTime orderTime;// 下单时间
    @Schema(description = "订单状态")
    private OrderStatus status;     // 状态（待支付、已支付、进行中、已完成、已取消）
    @Schema(description = "总金额")
    private BigDecimal totalAmount; // 总金额
    @Schema(description = "订单项列表")
    private List<OrderItem> items;  // 订单项列表
    // 地址、配送方式等其他字段
}
