package com.mo.api.dto;

import com.mo.common.enumeration.CouponType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(name = "优惠券DTO")
@Data
@NoArgsConstructor
public class CouponDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String name;
    private CouponType couponType;
    private String description;
    private BigDecimal value;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
