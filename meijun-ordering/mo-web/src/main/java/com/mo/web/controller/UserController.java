package com.mo.web.controller;

import com.mo.api.service.RedisService;
import com.mo.api.service.UserService;
import com.mo.common.constant.MessageConstant;
import com.mo.common.constant.RedisKeyConstant;
import com.mo.common.context.BaseContext;
import com.mo.common.enumeration.UserIdentity;
import com.mo.common.exception.UserNotLoginException;
import com.mo.common.result.Result;
import com.mo.entity.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "用户管理")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;

    @GetMapping("/info")
    @Operation(summary = "获取用户信息")
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = User.class)))
    public Result<User> info() throws ClassNotFoundException {
        String uuid = BaseContext.getCurrentId();
        if(uuid == null) {
            throw new UserNotLoginException(MessageConstant.USER_NOT_LOGIN);
        }

        String identity = (String) redisService.hGet(RedisKeyConstant.USER_IDENTITY, uuid.substring(0,4));
        UserIdentity ui = UserIdentity.fromString(identity);

        Class<?> clazz = Class.forName("com.mo.entity." + ui.getName());
        User user = (User) redisService.getEntity(uuid, clazz);

        return Result.success(user);
    }
}
