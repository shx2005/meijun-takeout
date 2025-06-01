package com.mo.api.vo;

import com.mo.common.constant.PayMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@Schema (name = "余额信息VO")
public class BalanceVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema (description = "余额")
    private BigDecimal balance;
     @Schema (description = "支付方式")
    private PayMethod payMethod;
     @Schema (description = "订单id")
    private Long orderId;
}
