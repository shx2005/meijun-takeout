package com.mo.api.dto;

import com.mo.entity.SetMealItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 套餐数据传输
 */
@Schema(name = "套餐信息")
@Data
public class SetMealDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "套餐id")
    private Long id;
    //分类id
    @Schema(description = "分类id")
    private Long categoryId;
    //套餐名称
    @Schema(description = "套餐名称")
    private String name;
    //套餐价格
    @Schema(description = "套餐价格")
    private BigDecimal price;
    //状态 0:停用 1:启用
    @Schema(description = "状态 0:停用 1:启用")
    private Integer status;
    //描述信息
    @Schema(description = "描述信息")
    private String description;
    //图片
    @Schema(description = "图片")
    private String image;
    //套餐菜品关系
    @Schema(description = "套餐菜品关系")
    private List<SetMealItem> setMealItems = new ArrayList<>();

}
