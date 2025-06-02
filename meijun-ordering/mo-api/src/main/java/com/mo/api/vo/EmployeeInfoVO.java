package com.mo.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Schema(description = "获取客服信息VO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeInfoVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "客服id")
    private Long employeeId;
     @Schema(description = "客服名字")
    private String employeeName;
     @Schema(description = "客服头像")
    private String employAvatar;
     @Schema(description = "客服电话")
    private String employeePhone;
}
