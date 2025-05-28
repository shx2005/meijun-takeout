package com.mo.web.controller;

import com.mo.api.dto.EmployeeDTO;
import com.mo.api.dto.EmployeePageQueryDTO;
import com.mo.api.dto.StoreDTO;
import com.mo.api.service.EmployeeService;
import com.mo.api.service.OrderService;
import com.mo.api.service.RedisService;
import com.mo.api.service.StoreService;
import com.mo.common.enumeration.OrderStatus;
import com.mo.common.result.PageResult;
import com.mo.common.result.Result;
import com.mo.entity.Employee;
import com.mo.entity.Order;
import com.mo.entity.Store;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    private EmployeeService employeeService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private RedisService redisService;

    @Operation(summary = "获取商户订单列表")
    @Parameters({
            @Parameter(name = "orderId", description = "订单ID", required = true)
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = Order.class)))
    @GetMapping("/{orderId}")
    public Result<Order> getOrderById( @PathVariable Long orderId){
        log.info("查询订单{}", orderId);
        Order order = (Order) redisService.getEntity("order:" + orderId, Order.class);
        if(order == null) order = orderService.getOrderById(orderId);
        if(order == null) return Result.error();
        redisService.setEntity("order:" + orderId, order);

        return Result.success(order);
    }

    @Operation(summary = "获取订单详情", description = "获取指定订单的详细信息")
    @Parameters({
            @Parameter(name = "orderId", description = "订单ID", required = true),
            @Parameter(name = "status", description = "订单状态", required = true)
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = Order.class)))
    @PutMapping("/orders/{orderId}/status")
    Result<Order> updateOrderStatus(@PathVariable Long orderId, Integer status){
        Order order = (Order) redisService.getEntity("order:" + orderId, Order.class);
        if(order == null) order = orderService.getOrderById(orderId);
        if(order == null) return Result.error();

        OrderStatus orderStatus = OrderStatus.fromValue(status);
        order.setStatus(orderStatus);

        orderService.updateOrderStatus(orderId, status);
        redisService.del("order:" + orderId);

        return Result.success(order);
    }

    @Operation(summary = "获取所有员工")
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = Employee.class)))
    @GetMapping("/staff")
    public Result<List<Employee>> getEmployee(){
        List<Employee> list = employeeService.getAllEmployee();

        return Result.success(list);
    }

    @Operation(summary = "获取员工分页")
    @Parameters({
            @Parameter(name = "employeePageQueryDTO", schema = @Schema(implementation = EmployeePageQueryDTO.class))
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = Employee.class)))
    @GetMapping("/staff/page")
    public PageResult getEmployeePage(EmployeePageQueryDTO employeePageQueryDTO){
        int pageNum = employeePageQueryDTO.getPageNum();
        int pageSize = employeePageQueryDTO.getPageSize();
        int offset = (pageNum - 1) * pageSize;
        int size = pageSize;

        List<Employee> list = employeeService.getEmployeePage(offset, size);

        return PageResult.success(list.size(), list, pageNum, pageSize);
    }

    @Operation(summary = "保存员工信息")
    @Parameters({
            @Parameter(name = "employee", schema = @Schema(implementation = Employee.class))
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = Employee.class)))
    @PostMapping("/staff")
    public Result<Employee> saveEmployee(EmployeeDTO employeeDTO){
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employeeService.saveEmployee(employee);

        return Result.success(employee);
    }

    @Operation(summary = "修改员工信息")
    @Parameters({
            @Parameter(name = "id", description = "员工ID", required = true),
            @Parameter(name = "employeeDTO", description = "员工参数", required = true, schema = @Schema(implementation = EmployeeDTO.class))
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @PutMapping("/staff/{id}")
    public Result<String> updateEmployee(@PathVariable Long id, EmployeeDTO employeeDTO){
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employee.setId(id);
        employeeService.updateEmployee(employee);

        return Result.success();
    }

    @Operation(summary = "删除员工")
    @Parameters({
            @Parameter(name = "id", description = "员工ID", required = true)
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @DeleteMapping("/staff/{id}")
    public Result<String> deleteEmployee(@PathVariable Long id){
        employeeService.deleteEmployee(id);

        return Result.success();
    }

    @Operation(summary = "获取门店列表")
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = Store.class)))
    @GetMapping("/stores")
    public Result<List<Store>> getStore(){
        List<Store> list = storeService.getStore();

        return Result.success(list);
    }

    @Operation(summary = "修改门店信息")
    @Parameters({
            @Parameter(name = "id", description = "门店ID", required = true),
            @Parameter(name = "storeDTO", description = "门店参数", required = true, schema = @Schema(implementation = StoreDTO.class))
    })
    @ApiResponse(responseCode = "200", description = "成功")
    @PutMapping("/stores/{id}")
    public Result<String> updateStore(@PathVariable Long id, StoreDTO storeDTO){
        Store store = new Store();
        BeanUtils.copyProperties(storeDTO, store);
        store.setId(id);
        storeService.updateStore(store);

        return Result.success();
    }
}
