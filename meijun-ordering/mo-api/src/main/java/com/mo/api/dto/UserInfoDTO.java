package com.mo.api.dto;

import com.mo.common.enumeration.UserIdentity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户信息更新
 */
@Schema(description = "用户信息更新")
@Data
public class UserInfoDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;
    @Schema(description = "姓名")
    protected String name;
    @Schema(description = "用户名")
    protected String username;
    @Schema(description = "密码")
    protected String password;
    @Schema(description = "用户身份")
    protected UserIdentity identity;
    @Schema(description = "手机号")
    protected String phoneNum;
    @Schema(description = "性别")
    protected String gender;
    @Schema(description = "地址")
    protected String address;
    @Schema(description = "头像")
    protected String avatar_url;
}
