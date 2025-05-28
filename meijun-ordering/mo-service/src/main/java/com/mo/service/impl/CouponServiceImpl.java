package com.mo.service.impl;

import com.mo.api.dto.CouponValidateDTO;
import com.mo.api.service.CouponService;
import com.mo.api.vo.CouponValidateVo;
import com.mo.common.constant.MessageConstant;
import com.mo.common.exception.CouponInvalidateException;
import com.mo.entity.Coupon;
import com.mo.entity.Order;
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

    public CouponValidateVo validateCoupon(CouponValidateDTO couponValidateDTO) {
        Coupon coupon = couponMapper.getCouponById(couponValidateDTO.getCouponId());
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(coupon.getEndTime()) && now.isBefore(coupon.getStartTime())) {
            Long orderId = couponValidateDTO.getOrderId();
            Order order = orderMapper.getOrderById(orderId);
            BigDecimal amount = getBigDecimal(order, coupon);
            return CouponValidateVo.builder()
                    .amount(amount)
                    .orderId(orderId)
                    .payType(couponValidateDTO.getPayType())
                    .build();
        }

        throw new CouponInvalidateException(MessageConstant.COUPON_INVALITE_BY_TIME);
    }

    private static BigDecimal getBigDecimal(Order order, Coupon coupon) {
        BigDecimal amount = order.getTotal();
        if (amount.compareTo(coupon.getMinAmount()) < 0 || amount.compareTo(coupon.getMaxAmount()) > 0) {
            throw new CouponInvalidateException(MessageConstant.COUPON_INVALITE_BY_AMOUNT);
        }
        switch (coupon.getType()){
            case fixed -> {
                amount = amount.subtract(coupon.getValue());
            }
            case percentage -> {
                amount = amount.multiply(BigDecimal.ONE.subtract(coupon.getValue()));
            }
        }
        return amount;
    }
}
