package com.mo.api.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class SendMessageVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String status;
}
