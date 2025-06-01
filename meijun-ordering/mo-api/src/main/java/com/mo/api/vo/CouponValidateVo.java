package com.mo.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Schema (name = "优惠券验证VO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponValidateVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema (description = "优惠后金额")
    private BigDecimal amount;
     @Schema (description = "订单id")
    private Long orderId;
     @Schema (description = "支付类型")
    private Integer payType;
}
