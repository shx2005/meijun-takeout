package com.mo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "优惠活动")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Promotion implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "图片")
    private String imgUrl;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
