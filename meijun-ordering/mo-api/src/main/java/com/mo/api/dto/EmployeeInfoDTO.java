package com.mo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema (name = "员工信息DTO")
public class EmployeeInfoDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema (description = "用户id")
    private Long userId;
     @Schema (description = "员工id")
    private Long employeeId;
}
