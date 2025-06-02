package com.mo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Schema(name = "发放优惠券DTO")
@Data
@NoArgsConstructor
public class DistributeCouponDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户id")
    private Long userId;
    @Schema(description = "优惠券id")
    private Long couponId;
    @Schema(description = "客户id")
    private List<Long> customerIds;
}
