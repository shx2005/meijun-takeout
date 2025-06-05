package com.mo.entity;
import com.mo.common.enumeration.AfterSaleStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import com.mo.common.enumeration.AfterSaleType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Schema(name = "售后信息")
@Data
@NoArgsConstructor
public class AfterSale implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "售后id")
    private Long id;
    @Schema(description = "订单id")
    private Long orderId;
    @Schema(description = "用户id")
    private Long userId;
    @Schema(description = "售后类型")
    private AfterSaleType type;
    @Schema(description = "售后原因")
    private String reason;
    @Schema(description = "售后内容")
    private String content;
    @Schema(description = "售后状态")
    private AfterSaleStatus status;
    @Schema(description = "售后创建时间")
    private String createTime;
    @Schema(description = "售后更新时间")
    private String updateTime;
}
