package com.mo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(name = "数据概览查询参数")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataOverViewQueryDTO implements Serializable {

    @Schema(description = "查询时间段的开始时间")
    private LocalDateTime begin;
    @Schema(description = "查询时间段的结束时间")
    private LocalDateTime end;
}
