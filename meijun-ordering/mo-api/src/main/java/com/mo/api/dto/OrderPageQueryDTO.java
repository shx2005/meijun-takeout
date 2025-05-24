package com.mo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(name = "订单分页查询参数")
@Data
public class OrderPageQueryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "页码")
    private int page;
    @Schema(description = "每页数据量")
    private int size;
    @Schema(description = "订单状态")
    private Integer status;
    @Schema(description = "用户id")
    private String uuid;

    //@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;
    //@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

}
