package com.mo.web.controller;

import com.mo.api.dto.OrderPageQueryDTO;
import com.mo.api.service.OrderService;
import com.mo.common.context.BaseContext;
import com.mo.common.result.PageResult;
import com.mo.common.result.Result;
import com.mo.entity.Order;
import com.mo.service.mapper.OrderMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.wrapper.BaseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "订单管理")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/overview")
    public Result<List<Order>> getAll(){
        List<Order> orders = orderService.getAll();

        return Result.success(orders);
    }

    @GetMapping("/page")
    public PageResult getPage(OrderPageQueryDTO orderPageQueryDTO){
        int pageNum = orderPageQueryDTO.getPage();
        int pageSize = orderPageQueryDTO.getSize();
        String uuid = BaseContext.getCurrentId();
        int offset = (pageNum - 1) * pageSize;
        int size = pageSize;

        List<Order> orders = orderService.getPage(offset, size, uuid);
        return PageResult.success(orders.size(), orders, pageNum, pageSize);
    }

    @GetMapping("/{orderId}")
    public Result<Order> getOrderById( @PathVariable Long orderId){
        Order order = orderService.getOrderById(orderId);
        return Result.success(order);
    }
}
