package com.mo.api.dto;

import com.mo.common.enumeration.MessageStaus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@Schema(description = "发送消息")
public class SendMessageDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户id")
    private Long userId;
    @Schema(description = "店员id")
    private Long employeeId;
    @Schema(description = "订单id")
    private Long orderId;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "内容")
    private String content;
    @Schema(name = "发送者类型", description = "0:用户,1:店员")
    private Integer senderType;
}
