package com.mo.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Schema(description = "支付异常VO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorHandleVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "重试token")
    private String retryToken;
    @Schema(description = "重定向url")
    private String redirectUrl;
    @Schema(description = "订单id")
    private Long orderId;
     @Schema(description = "支付类型")
    private String payType;
}
