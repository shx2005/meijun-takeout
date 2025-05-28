package com.mo.web.controller;

import com.mo.api.dto.BalanceDTO;
import com.mo.api.service.PaymentService;
import com.mo.api.service.RedisService;
import com.mo.api.vo.BalanceVO;
import com.mo.common.constant.RedisKeyConstant;
import com.mo.common.context.BaseContext;
import com.mo.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@Tag(name =  "支付管理")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private RedisService redisService;

    @Operation(summary = "支付")
    @Parameters({
            @Parameter(name = "balanceDTO", description = "支付参数", required = true, schema = @Schema(implementation = BalanceDTO.class))
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = BalanceVO.class)))
    @PostMapping("/balance")
    public Result<BalanceVO> pay(@RequestBody BalanceDTO balanceDTO){
        BalanceVO balanceVO = paymentService.pay(balanceDTO);

        return Result.success(balanceVO);
    }

    @Operation(summary = "查询余额")
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = String.class)))
    @GetMapping("/balance")
    public Result<String> showBalance(){
        String UUID = BaseContext.getCurrentId();
        Object obj = redisService.hGet(RedisKeyConstant.USER_ID, UUID);
        Long userId = ((Number) obj).longValue();
        String balance = paymentService.showBalance(userId);

        return Result.success(balance);
    }
}
