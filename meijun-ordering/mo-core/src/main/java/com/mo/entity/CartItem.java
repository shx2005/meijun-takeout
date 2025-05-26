package com.mo.entity;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.mo.common.enumeration.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Schema(name = "购物车项")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "购物车项id")
    private Long id;
    @Schema(description = "购物车项名称")
    private String name;
    @Schema(description = "购物车id")
    private Long cartId;
    @Schema(description = "用户id")
    private Long userId;
    @Schema(description = "购物车项类型")
    private Long itemId;
    @Schema(description = "购物车项类型")
    private ItemType itemType;
    @Schema(description = "购物车项数量")
    private Integer quantity;
    @Schema(description = "购物车项单价")
    private BigDecimal unitPrice;
    @Schema(description = "购物车项总价")
    private BigDecimal totalPrice;
}
