package com.mo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(name = "促销商品DTO")
@Data
@NoArgsConstructor
public class PromotionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema (description = "用户id")
    private Long userId;
     @Schema (description = "促销商品名字")
    private String name;
      @Schema (description = "促销商品描述")
    private String description;
      @Schema (description = "促销商品开始时间")
    private LocalDateTime startTime;
       @Schema (description = "促销商品结束时间")
    private LocalDateTime endTime;
}
