package com.mo.service.mapper;

import com.mo.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DishMapper {
    List<Dish> getCategories();
    List<Dish> getPage(@Param("offset") int offset,@Param("size") int size);
}
