package com.mo.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Schema(description = "撤回信息VO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawMessageVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "撤回信息id")
    private Long messageId;
     @Schema(description = "操作状态")
    private String status;
}
