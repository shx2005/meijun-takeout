package com.mo.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Schema(name = "添加优惠券VO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

     @Schema(description = "优惠券id")
    private Long couponId;
      @Schema(description = "操作状态")
    private String status;
}
