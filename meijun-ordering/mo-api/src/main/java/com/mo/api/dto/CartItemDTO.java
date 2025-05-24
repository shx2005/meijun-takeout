package com.mo.api.dto;

import com.mo.common.enumeration.ItemType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Schema(name = "购物车项信息")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户id")
    private Long userId;
    @Schema(description = "购物车项id")
    private Long ItemId;
    @Schema(description = "购物车项数量")
    private Integer quantity;
    @Schema(description = "购物车项类型")
    private ItemType itemType;
}
