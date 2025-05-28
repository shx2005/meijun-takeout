package com.mo.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponValidateVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private BigDecimal amount;
    private Long orderId;
    private Integer payType;
}
