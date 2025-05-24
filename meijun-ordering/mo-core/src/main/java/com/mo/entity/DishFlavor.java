package com.mo.entity;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Schema(description = "菜品口味关系表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DishFlavor implements Serializable{

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    //菜品id
    private Long dishId;
    //口味名称
    private String name;
    //口味数据list
    private String value;
}
