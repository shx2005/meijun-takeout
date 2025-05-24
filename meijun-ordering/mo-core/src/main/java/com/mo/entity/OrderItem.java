package com.mo.entity;

import com.mo.common.enumeration.ItemType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "订单项")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "订单项id")
    private Long id;

    @Schema(description = "订单id")
    private String orderId;
    @Schema(name = "项id", description = "订单项的内容")
    private String itemId;
    @Schema(name = "项类型", description = "订单项的内容类型")
    private ItemType itemType;
    @Schema(description = "数量")
    private Integer quantity;
    @Schema(description = "单价")
    private BigDecimal unitPrice;
    @Schema(description = "总价")
    private BigDecimal totalPrice;
}
