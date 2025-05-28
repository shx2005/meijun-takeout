package com.mo.web.controller;

import com.mo.api.dto.BalanceDTO;
import com.mo.api.service.PaymentService;
import com.mo.api.service.RedisService;
import com.mo.api.vo.BalanceVO;
import com.mo.common.constant.RedisKeyConstant;
import com.mo.common.context.BaseContext;
import com.mo.common.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@Tag(name =  "支付管理")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private RedisService redisService;

    @PostMapping("/balance")
    public Result<BalanceVO> pay(BalanceDTO balanceDTO){
        BalanceVO balanceVO = paymentService.pay(balanceDTO);

        return Result.success(balanceVO);
    }

    @GetMapping("/balance")
    public Result<String> showBalance(){
        String UUID = BaseContext.getCurrentId();
        Object obj = redisService.hGet(RedisKeyConstant.USER_ID, UUID);
        Long userId = ((Number) obj).longValue();
        String balance = paymentService.showBalance(userId);

        return Result.success(balance);
    }
}
