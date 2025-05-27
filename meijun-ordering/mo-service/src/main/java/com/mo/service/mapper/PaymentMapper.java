package com.mo.service.mapper;

import com.mo.api.vo.BalanceVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper {
    void savePayment(BalanceVO balanceVO);
}
