package com.mo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Schema(name = "同意请求DTO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApproveDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户id")
    private Long userId;
     @Schema(description = "售后服务id")
    private Long RequestId;
}
