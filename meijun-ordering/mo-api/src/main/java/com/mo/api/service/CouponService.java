package com.mo.api.service;

import com.mo.api.dto.CouponValidateDTO;
import com.mo.api.vo.CouponValidateVo;
import com.mo.entity.Coupon;

import java.util.List;

public interface CouponService {
    List<Coupon> getCouponByUserId(Long userId);
    CouponValidateVo validateCoupon(CouponValidateDTO couponValidateDTO);
}
