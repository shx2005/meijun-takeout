package com.mo.api.dto;

import com.mo.entity.OrderDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "订单传输对象")
@Data
public class OrdersDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "订单id")
    private Long id;
    //订单号
    @Schema(description = "订单号")
    private String number;
    //订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
    @Schema(description = "订单状态 1待付款，2待派送，3已派送，4已完成，5已取消")
    private Integer status;
    //下单用户id
    @Schema(description = "下单用户id")
    private Long userId;
    //餐具数量
    @Schema(description = "餐具数量")
    private int tablewareNumber;
    //餐具数量状态  1按餐量提供  0选择具体数量
    @Schema(description = "餐具数量状态  1按餐量提供  0选择具体数量")
    private Integer tablewareStatus;
    //地址id
    @Schema(description = "地址id")
    private Long addressBookId;
    //下单时间
    @Schema(description = "下单时间")
    private LocalDateTime orderTime;
    //结账时间
    @Schema(description = "结账时间")
    private LocalDateTime checkoutTime;
    //支付方式 1微信，2支付宝
    @Schema(description = "支付方式 1微信，2支付宝")
    private Integer payMethod;
    //实收金额
    @Schema(description = "实收金额")
    private BigDecimal amount;
    //备注
    @Schema(description = "备注")
    private String remark;
    //用户名
    @Schema(description = "用户名")
    private String userName;
    //手机号
    @Schema(description = "手机号")
    private String phone;
    //地址
    @Schema(description = "地址")
    private String address;
    //收货人
    @Schema(description = "收货人")
    private String consignee;
    @Schema(description = "订单明细")
    private List<OrderDetail> orderDetails;
    //配送状态  1立即送出  0选择具体时间
    @Schema(description = "配送状态  1立即送出  0选择具体时间")
    private Integer deliveryStatus;


}
