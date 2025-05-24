package com.mo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Schema(name = "套餐分页查询参数")
@Data
public class SetMealPageQueryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "页码")
    private int page;
    @Schema(description = "每页数据量")
    private int pageSize;
    @Schema(description = "套餐名称")
    private String name;
    //分类id
    @Schema(description = "套餐分类id")
    private Integer categoryId;
    //状态 0表示禁用 1表示启用
    @Schema(description = "套餐状态 0表示禁用 1表示启用")
    private Integer status;

}
