package com.mo.entity;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.mo.common.enumeration.CouponType;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(name = "优惠券信息")
@Data
public class Coupon implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;
    @Schema(description = "用户id")
    private Long userId;
    @Schema(description = "优惠券名称")
    private String name;
    @Schema(description = "优惠券描述")
    private String description;
    @Schema(description = "优惠券类型")
    private CouponType type;
    @Schema(description = "优惠券金额")
    private BigDecimal value;
    @Schema(description = "优惠券使用门槛")
    private BigDecimal minAmount;
    @Schema(description = "优惠券最大金额")
    private BigDecimal maxAmount;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
