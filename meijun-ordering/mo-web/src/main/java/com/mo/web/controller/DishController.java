package com.mo.web.controller;

import com.mo.api.dto.DishPageQueryDTO;
import com.mo.api.service.DishService;
import com.mo.common.result.PageResult;
import com.mo.common.result.Result;
import com.mo.entity.Dish;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/v1/dishes")
@Tag(name = "菜品管理")
public class DishController {
    @Autowired
    private DishService dishService;

    @GetMapping("/categories")
    @Tag(name = "获取菜品分类")
    public Result<List<Dish>> getCategories(){
        List<Dish> dishes = dishService.getCategories();

        return Result.success(dishes);
    }

    @GetMapping("/page")
    public PageResult getPage(DishPageQueryDTO dishPageQueryDTO){
        int pageNum = dishPageQueryDTO.getPageNum();
        int pageSize = dishPageQueryDTO.getPageSize();
        int offset = (pageNum - 1) * pageSize;
        int size = pageSize;

        List<Dish> list = dishService.getPage(offset,size);

        return PageResult.success();
    }
}
