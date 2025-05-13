package com.mo.service.impl;

import com.mo.api.dto.DishPageQueryDTO;
import com.mo.api.service.DishService;
import com.mo.entity.Dish;
import com.mo.service.mapper.DishMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Override
    public List<Dish> getCategories() {
        return null;
    }

    @Override
    public List<Dish> getPage(int offset, int size) {
        return null;
    }
}
