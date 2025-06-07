package com.mo.api.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class KaptchaVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String uuid;
    private String code;
}
