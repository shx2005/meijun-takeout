package com.mo.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema (description = "获取客户信息VO")
public class CustomerInfoVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema (description = "客户id")
    private Long CustomerId;
     @Schema (description = "客户名字")
    private String CustomerName;
      @Schema (description = "客户头像")
    private String CustomerAvatar;
}
