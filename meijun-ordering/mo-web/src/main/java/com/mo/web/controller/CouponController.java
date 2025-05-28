package com.mo.web.controller;

import com.mo.api.dto.CouponValidateDTO;
import com.mo.api.service.CouponService;
import com.mo.api.service.RedisService;
import com.mo.api.vo.CouponValidateVo;
import com.mo.common.constant.RedisKeyConstant;
import com.mo.common.context.BaseContext;
import com.mo.common.result.Result;
import com.mo.entity.Coupon;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "优惠券管理接口")
@Slf4j
@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {
    @Autowired
    private CouponService couponService;
    @Autowired
    private RedisService redisService;

    @Operation(summary = "获取优惠券", description = "获取用户优惠券")
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = Coupon.class)))
    @GetMapping("")
    public Result<List<Coupon>> getCoupons() {
        String uuid = BaseContext.getCurrentId();
        Object obj = redisService.hGet(RedisKeyConstant.USER_ID, uuid);
        Long id = ((Number) obj).longValue();
        List<Coupon> list = couponService.getCouponByUserId(id);
        return Result.success(list);
    }

    @Operation(summary = "验证优惠券", description = "验证优惠券")
    @Parameters({
            @Parameter(name = "CouponValidateDTO", description = "优惠券参数", required = true, schema = @Schema(implementation = CouponValidateDTO.class))
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = CouponValidateVo.class)))
    @PostMapping("/validate")
    public Result<CouponValidateVo> checkCoupon(CouponValidateDTO couponValidateDTO) {
        CouponValidateVo couponValidateVo = couponService.validateCoupon(couponValidateDTO);
        return Result.success(couponValidateVo);
    }
}
