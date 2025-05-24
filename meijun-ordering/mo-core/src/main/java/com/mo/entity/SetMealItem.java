package com.mo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "套餐项")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetMealItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;
    @Schema(description = "套餐id")
    private Long setMealId;
    @Schema(description = "菜品id")
    private Long dishId;
    @Schema(description = "份数")
    private Integer copies;
}