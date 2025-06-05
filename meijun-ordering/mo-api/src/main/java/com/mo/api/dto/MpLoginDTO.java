package com.mo.api.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class MpLoginDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String code;
}
