package com.mo.service.impl;

import com.mo.api.dto.BalanceDTO;
import com.mo.api.service.OrderService;
import com.mo.api.service.PaymentService;
import com.mo.api.service.RedisService;
import com.mo.api.vo.BalanceVO;
import com.mo.common.constant.MessageConstant;
import com.mo.common.enumeration.OrderPayStaus;
import com.mo.common.enumeration.PayMethod;
import com.mo.common.exception.BalanceNotEnoughException;
import com.mo.common.exception.PaymentBusinessException;
import com.mo.entity.Customer;
import com.mo.entity.Order;
import com.mo.service.mapper.CustomerMapper;
import com.mo.service.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public BalanceVO pay(BalanceDTO dto) {
        PayMethod method = dto.getMethod();
        Long userId = dto.getCustomerId();
        Long orderId = dto.getOrderId();
        BigDecimal amount = dto.getAmount();

        switch (method) {
            case cash_pay -> {
                Customer customer = customerMapper.getCustomerById(userId);
                BigDecimal balance = customer.getBalance();
                if (balance.compareTo(amount) < 0) {
                    throw new BalanceNotEnoughException(MessageConstant.INSUFFICIENT_BALANCE);
                }
                customer.setBalance(balance.subtract(amount));
                Order order = new Order();
                order.setId(orderId);
                order.setPayStatus(OrderPayStaus.paid);

                customerMapper.updateCustomer(customer);
                orderMapper.updateOrder(order);

                return BalanceVO.builder()
                        .balance(customer.getBalance())
                        .payMethod(method)
                        .orderId(orderId)
                        .build();
            }
            case wx_pay -> {
                log.info("微信支付");
                return BalanceVO.builder()
                        .balance(BigDecimal.ZERO)
                        .payMethod(method)
                        .orderId(orderId)
                        .build();
            }
            case ali_pay -> {
                log.info("支付宝支付");
                return BalanceVO.builder()
                        .balance(BigDecimal.ZERO)
                        .payMethod(method)
                        .orderId(orderId)
                        .build();
            }
            default -> throw new PaymentBusinessException(MessageConstant.UNKONWN_PAYMETHOD);
        }
    }

    @Override
    public String showBalance(Long UserId) {
        Customer customer = customerMapper.getCustomerById(UserId);
        BigDecimal balance = customer.getBalance();

        return balance.toString();
    }

    @Override
    public void refund(Long orderId){
        Order order = orderMapper.getOrderById(orderId);
        Long userId = order.getUserId();
        BigDecimal total = order.getTotal();

        Customer customer = customerMapper.getCustomerById(userId);
        customer.setBalance(customer.getBalance().add(total));
        customerMapper.updateCustomer(customer);
    }
}
