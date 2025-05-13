package com.mo.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 菜品分页查询
 */
@Data
public class DishPageQueryDTO implements Serializable {

    private int pageNum = 1;
    private int pageSize = 10;
    private String name;
    //分类id
    private Integer categoryId;
    //状态 0表示禁用 1表示启用
    private Integer status;

}
