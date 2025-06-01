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

    private Long userId;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
