package com.mo.entity;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(name = "分类信息")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "分类id")
    private Long id;
    @Schema(name = "分类类型", description = "0标识菜品分类 1标识套餐分类", allowableValues = "0,1")
    private Integer type;
    @Schema(description = "分类名称")
    private String name;
    @Schema(description = "排序")
    private Integer sort;
    @Schema(name = "状态",description = "0 禁用 1 启用" , allowableValues = "0,1")
    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private Long createUser;
    private Long updateUser;
}
