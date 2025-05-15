package com.mo.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Cart implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private List<CartItem> items;
}
