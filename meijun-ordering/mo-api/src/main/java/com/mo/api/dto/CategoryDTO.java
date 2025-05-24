package com.mo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

@Schema(name = "分类信息")
@Data
public class CategoryDTO implements Serializable {

    @Schema(description = "分类id")
    private Long id;
    @Schema(name = "分类类型", description = "0标识菜品分类 1标识套餐分类", allowableValues = "0,1")
    private Integer type;
    @Schema(description = "分类名称")
    private String name;
    @Schema(description = "排序")
    private Integer sort;

}
