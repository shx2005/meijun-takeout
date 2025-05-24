package com.mo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Schema(description = "订单评价")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderComment implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "订单评价id")
    private Long id;
    @Schema(description = "订单id")
    private Long orderId;
    @Schema(description = "内容")
    private String content;
}
