package com.mo.service.impl;

import com.mo.api.dto.CouponValidateDTO;
import com.mo.api.service.CouponService;
import com.mo.api.vo.CouponValidateVo;
import com.mo.common.constant.MessageConstant;
import com.mo.common.exception.CouponInvalidateException;
import com.mo.entity.Coupon;
import com.mo.entity.Order;
import com.mo.service.annotation.AutoFillTime;
import com.mo.service.mapper.CouponMapper;
import com.mo.service.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private OrderMapper orderMapper;

    public List<Coupon> getCouponByUserId(Long userId) {
        return couponMapper.getCouponByUserId(userId);
    }

    @Override
    public List<Coupon> getAllCoupons() {
        return couponMapper.getAllCoupons();
    }

    @Override
    public Coupon getCouponById(Long couponId) {
        return couponMapper.getCouponById(couponId);
    }

    @Override
    @AutoFillTime
    public Long saveCoupon(Coupon coupon) {
        couponMapper.saveCoupon(coupon);
        return coupon.getId();
    }

    @Override
    public CouponValidateVo validateCoupon(CouponValidateDTO couponValidateDTO) {
        Coupon coupon = couponMapper.getCouponById(couponValidateDTO.getCouponId());
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(coupon.getEndTime()) || now.isBefore(coupon.getStartTime())) {
            throw new CouponInvalidateException(MessageConstant.COUPON_INVALITE_BY_TIME);
        }

        Long orderId = couponValidateDTO.getOrderId();
        Order order = orderMapper.getOrderById(orderId);
        BigDecimal amount = getBigDecimal(order, coupon);

        return CouponValidateVo.builder()
                .amount(amount)
                .orderId(orderId)
                .payType(couponValidateDTO.getPayType())
                .build();
    }

    @Override
    public void deleteCouponById(Long couponId) {
        couponMapper.deleteCouponById(couponId);
    }

    @Override
    @AutoFillTime
    public void updateCoupon(Coupon coupon) {
        couponMapper.updateCoupon(coupon);
    }

    private static BigDecimal getBigDecimal(Order order, Coupon coupon) {
        BigDecimal amount = order.getTotal();
        if (amount.compareTo(coupon.getMinAmount()) < 0 || amount.compareTo(coupon.getMaxAmount()) > 0) {
            throw new CouponInvalidateException(MessageConstant.COUPON_INVALITE_BY_AMOUNT);
        }
        return switch (coupon.getType()){
            case fixed -> amount.subtract(coupon.getValue());
            case percentage -> amount.multiply(coupon.getValue().divide(new BigDecimal("100")));
        };
    }
}
