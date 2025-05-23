package com.mo.web.controller;

import com.mo.api.service.UserService;
import com.mo.common.constant.MessageConstant;
import com.mo.common.context.BaseContext;
import com.mo.common.exception.UserNotLoginException;
import com.mo.common.result.Result;
import com.mo.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/info")
    @Operation(summary = "获取用户信息")
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = User.class)))
    public Result<User> info(){
        String uuid = BaseContext.getCurrentId();
        if(uuid == null) {
            throw new UserNotLoginException(MessageConstant.USER_NOT_LOGIN);
        }

        User user = (User) redisTemplate.opsForValue().get(uuid);
        
        return Result.success(user);
    }
}
