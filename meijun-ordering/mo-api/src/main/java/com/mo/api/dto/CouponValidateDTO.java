package com.mo.api.dto;

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
public class CouponValidateDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long couponId;
    private Long orderId;
    private BigDecimal amount;
    private Integer payType;
}
