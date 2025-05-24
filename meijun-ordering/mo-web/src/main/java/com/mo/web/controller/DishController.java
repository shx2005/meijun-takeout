package com.mo.web.controller;

import com.mo.api.dto.DishPageQueryDTO;
import com.mo.api.service.DishService;
import com.mo.common.result.PageResult;
import com.mo.common.result.Result;
import com.mo.entity.Dish;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "获取菜品分类")
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = List.class)))
    @GetMapping("/categories")
    public Result<List<Dish>> getCategories(){
        List<Dish> dishes = dishService.getCategories();

        return Result.success(dishes);
    }

    @Operation(summary = "获取菜品分页")
    @Parameters({
            @Parameter(name = "dishPageQueryDTO", schema = @Schema(implementation = DishPageQueryDTO.class))
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = PageResult.class)))
    @GetMapping("/page")
    public PageResult getPage(DishPageQueryDTO dishPageQueryDTO){
        int pageNum = dishPageQueryDTO.getPageNum();
        int pageSize = dishPageQueryDTO.getPageSize();
        int offset = (pageNum - 1) * pageSize;
        int size = pageSize;

        List<Dish> list = dishService.getPage(offset,size);

        return PageResult.success(list.size(), list, pageNum, pageSize);
    }
}
