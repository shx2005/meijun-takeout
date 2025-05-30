package com.mo.api.vo;

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
public class SendMessageVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String status;
}
