package com.mo.web.controller;

import com.mo.api.dto.OrderSubmitDTO;
import com.mo.api.service.CartService;
import com.mo.api.service.OrderService;
import com.mo.api.vo.OrderSubmitVO;
import com.mo.common.enumeration.ItemType;
import com.mo.common.result.Result;
import com.mo.entity.CartItem;
import com.mo.entity.Order;
import com.mo.web.MoWebApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = MoWebApplication.class)
@Sql({"/sql/insert-test-merchant.sql", "/sql/insert-test-dish.sql", "/sql/insert-test-customer.sql"})
public class OrderControllerTest {
    @Autowired
    OrderController orderController;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;

    @Test
    public void testSubmitOrder(){
    // 1. 添加购物车商品
    CartItem cartItem = new CartItem();
    cartItem.setItemId(1L);
    cartItem.setItemType(ItemType.fromValue(1)); // 假设为普通菜品类型
    cartItem.setName("宫保鸡丁");
    cartItem.setUserId(1L);
    cartItem.setQuantity(2); // 数量为2
    cartItem.setUnitPrice(new BigDecimal("25.00")); // 单价25元
    cartService.addToCart(cartItem);

    // 2. 构建 OrderSubmitDTO
    OrderSubmitDTO orderDTO = new OrderSubmitDTO();
    orderDTO.setUserId(1L);
    orderDTO.setAddressBookId(1L);
    orderDTO.setTablewareNumber(2); // 餐具数量2套
    orderDTO.setTablewareStatus(1); // 按餐量提供
    orderDTO.setPayMethod(1); // 微信支付
    orderDTO.setRemark("请尽快送达");
    orderDTO.setDeliveryStatus(1); // 立即送出
    orderDTO.setTotal(new BigDecimal("50.00"));

    // 3. 调用接口并验证结果
    Result<OrderSubmitVO> result = orderController.submitOrder(orderDTO);

    assertNotNull(result);
    assertTrue(result.isSuccess()); // 假设 Result 有 isSuccess 方法判断是否成功
    OrderSubmitVO vo = result.getData();
    assertNotNull(vo);
    assertNotNull(vo.getId());
    assertNotNull(vo.getOrderNumber());
    assertTrue(vo.getOrderAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    public void testGetOrderById(){
        Order order = orderService.getOrderById(1L);
        assertNotNull(order);
        assertNotNull(order.getStatus());
    }
}
