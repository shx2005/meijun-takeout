package com.mo.api.dto;

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
public class OrderStatusDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer status;
}
