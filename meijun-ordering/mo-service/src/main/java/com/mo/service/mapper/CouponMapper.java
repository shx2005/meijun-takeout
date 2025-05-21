package com.mo.service.mapper;

import com.mo.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CouponMapper {
    List<Coupon> getCouponByUserId(Long userId);
}
