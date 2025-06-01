package com.mo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Schema
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorHandleDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long orderId;
    private Long userId;
    private String payType;
}
