package com.mo.entity;

import com.mo.common.enumeration.ItemType;

import java.io.Serial;
import java.io.Serializable;

public class CartItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long itemId;
    private ItemType itemType;
    private Integer quantity;
}
