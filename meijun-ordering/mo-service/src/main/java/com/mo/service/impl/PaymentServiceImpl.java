package com.mo.service.impl;

import com.mo.api.dto.BalanceDTO;
import com.mo.api.service.PaymentService;
import com.mo.api.service.RedisService;
import com.mo.api.vo.BalanceVO;
import com.mo.common.constant.PayMethod;
import com.mo.common.exception.BalanceNotEnoughException;
import com.mo.entity.Customer;
import com.mo.service.mapper.CustomerMapper;
import com.mo.service.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.io.Serial;
import java.math.BigDecimal;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RedisService redisService;

    @Override
    public BalanceVO pay(BalanceDTO balanceDTO){
        Integer payMethod = balanceDTO.getPayMethod();
        if(payMethod.equals(PayMethod.CASH_PAY)){
            Long id = balanceDTO.getCustomerId();
            Customer customer = customerMapper.getCustomerById(id);
            BigDecimal balance = customer.getBalance();
            BigDecimal amount = balanceDTO.getAmount();
            if(balance.compareTo(amount) < 0) throw new BalanceNotEnoughException("余额不足");

            customer.setBalance(balance.subtract(amount));
            customerMapper.updateCustomer(customer);
            orderMapper.updateOrderStatus(balanceDTO.getOrderId(), 1);
        }

        return BalanceVO.builder()
                .balance(BigDecimal.ONE)
                .orderId(balanceDTO.getOrderId())
                .build();
    }
}
