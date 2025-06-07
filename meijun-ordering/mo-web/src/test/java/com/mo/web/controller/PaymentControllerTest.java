package com.mo.web.controller;

import com.mo.api.dto.BalanceDTO;
import com.mo.api.vo.BalanceVO;
import com.mo.common.enumeration.PayMethod;
import com.mo.common.result.Result;
import com.mo.web.MoWebApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@Slf4j
@SpringBootTest(classes = MoWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerTest {
    @Autowired
    private PaymentController paymentController;

    @Test
    public void pay() {
        BalanceDTO dto = BalanceDTO.builder()
                .amount(new BigDecimal(100))
                .orderId(6L)
                .customerId(2L)
                .method(PayMethod.cash_pay)
                .build();

        Result<BalanceVO> result = paymentController.pay(dto);
        BalanceVO vo = result.getData();

        log.info("{}", vo);
    }
}
