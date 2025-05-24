package com.mo.entity;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "菜品")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Dish implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;
    @Schema(description = "菜品名称")
    private String name;
    @Schema(description = "菜品分类id")
    private Long categoryId;
    @Schema(description = "价格")
    private BigDecimal price;
    @Schema(description = "图片")
    private String image;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "状态 0 停售 1 起售")
    private Integer status;
    @Schema(description = "销量")
    private Long sale;


    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private String createUser;
    private String updateUser;
}
