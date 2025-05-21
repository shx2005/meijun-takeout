package com.mo.entity;

import com.mo.common.enumeration.AfterSaleType;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AfterSale implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long orderId;
    private Long userId;
    private AfterSaleType type;
    private String reason;
    private String content;
    private Integer status;
    private String createTime;
    private String updateTime;
}
