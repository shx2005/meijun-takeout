package com.mo.api.service;

import com.mo.entity.Dish;

import java.util.List;

public interface DishService {
    List<Dish> getCategories();
    List<Dish> getPage(int offset, int size);
}
