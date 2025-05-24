package com.mo.entity;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "购物车")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cart implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "购物车id")
    private Long id;
    @Schema(description = "用户id")
    private Long userId;
    @Schema(description = "购物车项")
    private List<CartItem> items;

    LocalDateTime createTime;
    LocalDateTime updateTime;
}
