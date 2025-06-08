package com.mo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@Schema(name = "WebSocket消息")
public class WsMessageDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "消息内容")
    private String message;
}
