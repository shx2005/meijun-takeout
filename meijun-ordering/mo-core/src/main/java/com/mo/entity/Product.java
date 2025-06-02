package com.mo.entity;

import com.mo.common.enumeration.ItemType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Schema(description = "商品")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema (description = "商品id")
    private Long productId;
    @Schema (description = "商品名称")
    private String name;
    @Schema (description = "商品类型")
    private ItemType type;
     @Schema (description = "商品销量")
    private Long sales;
}
