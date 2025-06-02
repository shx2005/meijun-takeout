package com.mo.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Schema(description = "促销VO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "促销id")
    private Long promotionId;
     @Schema(description = "促销状态")
    private String status;
}
