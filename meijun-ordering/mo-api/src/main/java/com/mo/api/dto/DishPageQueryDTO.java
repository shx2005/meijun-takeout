package com.mo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 菜品分页查询
 */
@Schema(name = "菜品分页查询")
@Data
public class DishPageQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "页码")
    private int pageNum = 1;
    @Schema(description = "每页数据量")
    private int pageSize = 20;
    @Schema(description = "菜品名称")
    private String name;
    //分类id
    @Schema(description = "菜品分类id")
    private Integer categoryId;
    //状态 0表示禁用 1表示启用
    @Schema(description = "菜品状态 0表示禁用 1表示启用")
    private Integer status;

}
