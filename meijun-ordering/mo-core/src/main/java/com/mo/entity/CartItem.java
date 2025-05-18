package com.mo.entity;

import com.mo.common.enumeration.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long cartId;
    private Long userId;
    private Long itemId;
    private ItemType itemType;
    private Integer quantity;
}
