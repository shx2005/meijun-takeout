package com.mo.entity;

import com.mo.common.enumeration.ItemType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "订单详情")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetail implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "订单详情id")
    private Long id;
    @Schema(description = "订单详情名称")
    private String name;
    @Schema(description = "订单id")
    private Long orderId;
    @Schema(description = "菜品id")
    private Long ItemId;
    @Schema(description = "菜品类型")
    private ItemType itemType;
    @Schema(description = "菜品口味")
    private String dishFlavor;
    @Schema(description = "数量")
    private Integer quantity;
    @Schema(description = "单价")
    private BigDecimal unit;
    @Schema(description = "总价")
    private BigDecimal total;
    @Schema(description = "图片")
    private String image;
}
