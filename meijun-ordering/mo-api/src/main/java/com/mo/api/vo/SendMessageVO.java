package com.mo.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
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

     @Schema(description = "发送信息id")
    private Long id;
     @Schema (description = "发送信息状态")
    private String status;
}
