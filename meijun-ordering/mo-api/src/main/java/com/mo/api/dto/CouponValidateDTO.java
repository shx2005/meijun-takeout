package com.mo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "优惠券验证DTO")
public class CouponValidateDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "优惠券id")
    private Long couponId;
    @Schema(description = "订单id")
    private Long orderId;
    @Schema(description = "订单金额")
    private BigDecimal amount;
    @Schema(description = "支付类型")
    private Integer payType;
}
