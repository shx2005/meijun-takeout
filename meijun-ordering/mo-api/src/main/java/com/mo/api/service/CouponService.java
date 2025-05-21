package com.mo.api.service;

import com.mo.entity.Coupon;

import java.util.List;

public interface CouponService {
    List<Coupon> getCouponByUserId(Long userId);
}
