package com.mo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@Schema(name = "管理员分页查询参数")
public class AdminPageQueryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "姓名")
    private String name;
    @Schema(description = "页码")
    @Builder.Default
    private int pageNum = 1;
    @Schema(description = "每页数据量")
    @Builder.Default
    private int pageSize = 10;
}
