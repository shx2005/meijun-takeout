package com.mo.api.dto;

import java.io.Serial;
import java.io.Serializable;

public class OrderCommentDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long orderId;
    private String comment;
}
