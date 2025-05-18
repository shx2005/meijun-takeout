package com.mo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cart implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    private List<CartItem> items;

    LocalDateTime createTime;
    LocalDateTime updateTime;
}
