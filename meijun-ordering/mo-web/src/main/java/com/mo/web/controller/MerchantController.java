package com.mo.web.controller;

import com.mo.api.dto.EmployeePageQueryDTO;
import com.mo.api.service.EmployeeService;
import com.mo.api.service.OrderService;
import com.mo.common.enumeration.OrderStatus;
import com.mo.common.result.PageResult;
import com.mo.common.result.Result;
import com.mo.entity.Employee;
import com.mo.entity.Order;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/merchants")
@Tag(name = "商户管理")
public class MerchantController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/{orderId}")
    public Result<Order> getOrderById( @PathVariable Long orderId){
        log.info("查询订单{}", orderId);
        Order order = (Order) redisTemplate.opsForValue().get("order:" + orderId);
        if(order == null) order = orderService.getOrderById(orderId);
        if(order == null) return Result.error();
        redisTemplate.opsForValue().set("order:" + orderId, order);

        return Result.success(order);
    }

    @PutMapping("/orders/{orderId}/status")
    Result<Order> updateOrderStatus(@PathVariable Long orderId, Integer status){
        Order order = (Order) redisTemplate.opsForValue().get("order:" + orderId);
        if(order == null) order = orderService.getOrderById(orderId);
        if(order == null) return Result.error();

        OrderStatus orderStatus = OrderStatus.fromValue(status);
        order.setStatus(orderStatus);

        orderService.updateOrderStatus(orderId, status);
        redisTemplate.delete("order:" + orderId);

        return Result.success(order);
    }

    @GetMapping("/staff")
    public Result<List<Employee>> getStaff(){
        List<Employee> list = employeeService.getAllEmployee();

        return Result.success(list);
    }

    @GetMapping("/staff/page")
    public PageResult getEmployeePage(EmployeePageQueryDTO employeePageQueryDTO){
        int pageNum = employeePageQueryDTO.getPageNum();
        int pageSize = employeePageQueryDTO.getPageSize();
        int offset = (pageNum - 1) * pageSize;
        int size = pageSize;

        List<Employee> list = employeeService.getEmployeePage(offset, size);

        return PageResult.success();
    }
}
