package com.mo.entity;

import com.mo.common.enumeration.ItemType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public class OrderItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String orderId;
    private String itemId;
    private ItemType itemType;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
