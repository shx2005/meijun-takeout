package com.mo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "门店信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Store implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "门店id")
    Long id;
    @Schema(description = "商户id")
    Long merchantId;
    @Schema(description = "门店名称")
    String name;
    @Schema(description = "门店地址")
    String address;
    @Schema(description = "门店简介")
    String info;

    LocalDateTime createTime;
    LocalDateTime updateTime;
}
