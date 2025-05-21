package com.mo.entity;

import com.mo.common.enumeration.CouponType;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Coupon implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String name;
    private String description;
    private CouponType type;
    private BigDecimal value;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
