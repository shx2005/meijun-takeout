package com.mo.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单提交返回对象
 */
@Schema(description = "订单提交VO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderSubmitVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "订单提交id")
    //订单id
    private Long id;
    @Schema(description = "订单号")
    //订单号
    private String orderNumber;
    @Schema(description = "订单金额")
    //订单金额
    private BigDecimal orderAmount;
    @Schema(description = "下单时间")
    //下单时间
    private LocalDateTime orderTime;
}
