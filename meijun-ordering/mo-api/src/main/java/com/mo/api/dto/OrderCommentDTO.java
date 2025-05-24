package com.mo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Schema(name = "订单评价参数")
@Data
@Builder
public class OrderCommentDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "订单评价id")
    private Long id;
    @Schema(description = "订单id")
    private Long orderId;
    @Schema(description = "订单评价内容")
    private String comment;
}
