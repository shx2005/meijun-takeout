package com.mo.api.dto;

import com.mo.entity.DishFlavor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Schema(name = "菜品信息")
@Data
public class DishDTO implements Serializable {

    @Schema(description = "菜品id")
    private Long id;
    @Schema(description = "菜品名称")
    private String name;
    @Schema(description = "菜品分类id")
    private Long categoryId;
    @Schema(description = "菜品价格")
    private BigDecimal price;
    @Schema(description = "图片")
    private String image;
    @Schema(description = "菜品描述")
    private String description;
    //0 停售 1 起售
    @Schema(description = "状态 0 停售 1 起售")
    private Integer status;
    //口味
    @Schema(description = "口味")
    private List<DishFlavor> flavors = new ArrayList<>();

}
