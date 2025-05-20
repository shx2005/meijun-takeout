package com.mo.web.controller;

import com.mo.api.dto.EmployeeDTO;
import com.mo.api.dto.EmployeePageQueryDTO;
import com.mo.api.dto.StoreDTO;
import com.mo.api.service.EmployeeService;
import com.mo.api.service.OrderService;
import com.mo.api.service.StoreService;
import com.mo.common.enumeration.OrderStatus;
import com.mo.common.result.PageResult;
import com.mo.common.result.Result;
import com.mo.entity.Employee;
import com.mo.entity.Order;
import com.mo.entity.Store;
import com.mo.service.impl.StoreServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
    @Autowired
    private StoreService storeService;

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
    public Result<List<Employee>> getEmployee(){
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

    @PostMapping("/staff")
    public Result<Employee> saveEmployee(EmployeeDTO employeeDTO){
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employeeService.saveEmployee(employee);

        return Result.success(employee);
    }

    @PutMapping("/staff/{id}")
    public Result<String> updateEmployee(@PathVariable Long id, EmployeeDTO employeeDTO){
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employee.setId(id);
        employeeService.updateEmployee(employee);

        return Result.success();
    }

    @DeleteMapping("/staff/{id}")
    public Result<String> deleteEmployee(@PathVariable Long id){
        employeeService.deleteEmployee(id);

        return Result.success();
    }

    @GetMapping("/stores")
    public Result<List<Store>> getStore(){
        List<Store> list = storeService.getAll();

        return Result.success(list);
    }

    @PutMapping("/stores/{id}")
    public Result<String> updateStore(@PathVariable Long id, StoreDTO storeDTO){
        Store store = new Store();
        BeanUtils.copyProperties(storeDTO, store);
        store.setId(id);
        storeService.updateStore(store);

        return Result.success();
    }
}
