package com.mo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@Schema(name = "管理员保存参数")
public class AdminSaveDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    private Long id;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "密码")
    private String password;
}
