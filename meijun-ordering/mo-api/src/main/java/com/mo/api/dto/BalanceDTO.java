package com.mo.api.dto;

import com.mo.common.enumeration.PayMethod;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Tag(name = "余额信息")
public class BalanceDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long orderId;
    private Long customerId;
    private PayMethod method;
    private BigDecimal amount;
}
