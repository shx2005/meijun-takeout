package com.mo.api.vo;

import com.mo.common.constant.PayMethod;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
public class BalanceVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private BigDecimal balance;
    private PayMethod payMethod;
    private Long orderId;
}
