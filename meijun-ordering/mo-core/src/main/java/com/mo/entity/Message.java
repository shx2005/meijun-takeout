package com.mo.entity;

import com.mo.common.enumeration.MessageStaus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "消息")
public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;
    @Schema(description = "用户id")
    private Long userId;
    @Schema(description = "店员id")
    private Long employeeId;
    @Schema(description = "订单id")
    private Long orderId;
    @Schema(description = "消息状态")
    private MessageStaus staus;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "内容")
    private String content;
    @Schema(name = "发送者类型", description = "0:用户,1:店员")
    private Integer senderType;

    LocalDateTime createTime;
    LocalDateTime updateTime;
}
