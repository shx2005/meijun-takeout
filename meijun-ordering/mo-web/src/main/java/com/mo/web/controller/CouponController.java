package com.mo.web.controller;

import com.mo.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {

    @GetMapping("")
    public Result getCoupons() {
        return "coupons";
    }
}
