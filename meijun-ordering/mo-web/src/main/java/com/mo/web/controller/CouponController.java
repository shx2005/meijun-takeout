package com.mo.web.controller;

import com.mo.api.service.CouponService;
import com.mo.common.constant.RedisKeyConstant;
import com.mo.common.result.Result;
import com.mo.entity.Coupon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {
    @Autowired
    private CouponService couponService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("")
    public Result<List<Coupon>> getCoupons() {
        Long id = (Long) redisTemplate.opsForValue().get(RedisKeyConstant.USER_ID);
        List<Coupon> list = couponService.getCouponByUserId(id);
        return Result.success(list);
    }
}
