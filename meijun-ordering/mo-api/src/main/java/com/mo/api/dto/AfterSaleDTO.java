package com.mo.api.dto;

import com.mo.common.enumeration.AfterSaleType;

import java.io.Serializable;

public class AfterSaleDTO implements Serializable {

    private Long orderId;
    private Long userId;
    private AfterSaleType type;
    private String reason;
    private String content;
}
