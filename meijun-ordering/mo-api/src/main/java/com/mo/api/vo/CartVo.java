package com.mo.api.vo;

import com.mo.entity.CartItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "购物车id")
    private Long id;
    @Schema(description = "用户id")
    private Long userId;
    @Schema(description = "购物车项")
    private List<CartItem> items;
    @Schema(description = "总价")
    private BigDecimal total;
}
