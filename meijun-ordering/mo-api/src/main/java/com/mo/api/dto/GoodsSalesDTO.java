package com.mo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Schema
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoodsSalesDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    //商品名称
    @Schema(description = "商品名称")
    private String name;
    //销量
    @Schema(description = "销量")
    private Integer number;
}
