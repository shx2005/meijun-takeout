package com.mo.web.controller;

import com.mo.api.dto.AfterSaleDTO;
import com.mo.api.dto.OrderPageQueryDTO;
import com.mo.api.dto.OrderCommentDTO;
import com.mo.api.service.OrderService;
import com.mo.common.context.BaseContext;
import com.mo.common.result.PageResult;
import com.mo.common.result.Result;
import com.mo.entity.AfterSale;
import com.mo.entity.Order;
import com.mo.entity.OrderComment;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "获取订单列表")
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = Order.class)))
    @GetMapping("/overview")
    public Result<List<Order>> getAll(){
        List<Order> orders = orderService.getAll();

        return Result.success(orders);
    }

    @Operation(summary = "获取订单分页")
    @Parameters({
            @Parameter(name = "orderPageQueryDTO", schema = @Schema(implementation = OrderPageQueryDTO.class))
    })
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

    @Operation(summary = "获取订单详情")
    @Parameters({
            @Parameter(name = "orderId", description = "订单ID", required = true)
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = Order.class)))
    @GetMapping("/{orderId}")
    public Result<Order> getOrderById( @PathVariable Long orderId){
        Order order = orderService.getOrderById(orderId);

        return Result.success(order);
    }

    @Operation(summary = "获取订单分页")
    @Parameters({
            @Parameter(name = "orderCommentQueryDTO", schema = @Schema(implementation = OrderCommentDTO.class))
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = OrderComment.class)))
    @PostMapping("/comment")
    public Result<OrderComment> saveOrderComment(OrderCommentDTO orderCommentDTO){
        Long orderId = orderCommentDTO.getOrderId();
        String content = orderCommentDTO.getComment();
        OrderComment orderComment = orderService.saveOrderComment(orderId, content);

        return Result.success(orderComment);
    }

    @Operation(summary = "保存订单评价")
    @Parameters({
            @Parameter(name = "orderAfterSaleDTO", description = "订单售后参数", required = true, schema = @Schema(implementation = AfterSaleDTO.class))
    })
    @PostMapping("/after-sale")
    public Result<String> applyForAfterSale(AfterSaleDTO afterSaleDTO){
        AfterSale afterSale = new AfterSale();
        BeanUtils.copyProperties(afterSaleDTO, afterSale);
        orderService.saveAfterSale(afterSale);

        return Result.success();
    }
}
