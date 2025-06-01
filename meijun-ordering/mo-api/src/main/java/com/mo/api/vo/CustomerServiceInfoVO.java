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
public class CustomerServiceInfoVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long employeeId;
    private String employeeName;
    private String employAvatar;
    private String employeePhone;
}
