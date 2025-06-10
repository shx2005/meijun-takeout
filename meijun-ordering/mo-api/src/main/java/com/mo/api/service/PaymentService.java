package com.mo.api.service;

import com.mo.api.dto.BalanceDTO;
import com.mo.api.vo.BalanceVO;

public interface PaymentService {

    BalanceVO pay(BalanceDTO balanceDTO);

    String showBalance(Long UserId);

    void refund(Long orderId);
}
