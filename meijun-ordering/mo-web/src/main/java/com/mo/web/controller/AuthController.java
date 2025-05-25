package com.mo.web.controller;

import com.mo.api.dto.AuthLoginDTO;
import com.mo.api.dto.AuthRegisterDTO;
import com.mo.api.service.RedisService;
import com.mo.api.vo.AuthLoginVo;
import com.mo.common.constant.JwtClaimsConstant;
import com.mo.common.constant.MessageConstant;
import com.mo.common.constant.RedisKeyConstant;
import com.mo.common.context.BaseContext;
import com.mo.common.enumeration.UserIdentity;
import com.mo.common.exception.RedisAccessException;
import com.mo.common.exception.UnknownIdentityException;
import com.mo.common.exception.UserNotLoginException;
import com.mo.common.properties.JwtProperties;
import com.mo.common.result.Result;
import com.mo.common.utils.JwtUtil;
import com.mo.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import com.mo.api.service.AuthService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "账号管理")
public class AuthController {
    //todo 可以改为构造器注入
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private RedisTemplate<String, Object>  redisTemplate;
    @Autowired
    private RedisService redisService;

    @Operation(summary = "登录")
    @Parameters({
            @Parameter(name = "AuthLoginDTO", description = "登录参数", required = true, schema = @Schema(implementation = AuthLoginDTO.class))
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = AuthLoginVo.class)))
    @PostMapping("/login")
    public Result<AuthLoginVo> login(@RequestBody AuthLoginDTO authLoginDTO) {
        log.info("login:{}", authLoginDTO);

        User user = authService.login(authLoginDTO);

        Map<String, Object> claims = new HashMap<>();
        String id = JwtClaimsConstant.getId(user.getIdentity());
        claims.put(id, user.getUuid());

        // 生成JWT令牌
        String token = JwtUtil.createJwt(
                getKey(user.getIdentity()),
                getTtl(user.getIdentity()),
                claims);

        //放入当前线程
        String uuid = user.getUuid();
        redisService.hSet(RedisKeyConstant.USER_IDENTITY, uuid.substring(0,4), user.getIdentity().getName());
        redisService.setEntity(uuid, user);
        BaseContext.setCurrentId(uuid);
        //todo 检查输入是否合法
        //todo 验证码
        AuthLoginVo authLoginVo = AuthLoginVo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .uuid(user.getUuid())
                .token(token)
                .build();

        return Result.success(authLoginVo);
    }

    @Operation(summary = "微信小程序登录")
    @Parameters({
            @Parameter(name = "loginData", description = "登录参数", required = true, schema = @Schema(implementation = Map.class))
    })
    @PostMapping("/mp/login")
    public Result<AuthLoginVo> mpLogin(@RequestBody Map<String, String> loginData) {
        log.info("微信小程序登录: {}", loginData);
        
        // 获取微信小程序传来的code等信息
        String code = loginData.get("code");
        String encryptedData = loginData.get("encryptedData");
        String getPhoneCode = loginData.get("getPhoneCode");
        
        // 这里应该调用微信API获取openid等信息
        // 为了演示，我们直接构造一个模拟用户
        User user = new User();
        user.setId(1L); // 假设用户ID为1
        user.setUsername("微信用户");
        user.setName("微信用户");
        user.setUuid(java.util.UUID.randomUUID().toString());
        user.setIdentity(UserIdentity.CUSTOMER);
        
        Map<String, Object> claims = new HashMap<>();
        String id = JwtClaimsConstant.getId(user.getIdentity());
        claims.put(id, user.getId());

        // 生成JWT令牌
        String token = JwtUtil.createJwt(
                getKey(user.getIdentity()),
                getTtl(user.getIdentity()),
                claims);

        //放入当前线程
        redisTemplate.opsForValue().set(RedisKeyConstant.USER_ID, user.getId());
        redisTemplate.opsForValue().set(user.getUuid(), user, getTtl(user.getIdentity()), TimeUnit.MILLISECONDS);
        BaseContext.setCurrentId(user.getUuid());
        
        AuthLoginVo authLoginVo = AuthLoginVo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .uuid(user.getUuid())
                .token(token)
                .build();

        return Result.success(authLoginVo);
    }

    @Operation(summary = "微信小程序登录")
    @Parameters({
            @Parameter(name = "AuthRegisterDTO", description = "注册参数", required = true, schema = @Schema(implementation = AuthRegisterDTO.class))
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = User.class)))
    @PostMapping("/register")
    public Result<User> register(@RequestBody AuthRegisterDTO authRegisterDTO) {
        log.info("register:{}", authRegisterDTO);

        User user = new User();
        BeanUtils.copyProperties(authRegisterDTO, user);
        authService.saveUser(user);

        return Result.success(user);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<String> logout(){
        String uuid = BaseContext.getCurrentId();
        log.info("logout: {}", uuid);
        BaseContext.removeCurrentId();
        redisTemplate.delete(uuid);
        redisTemplate.delete(RedisKeyConstant.USER_ID);
        return Result.success();
    }

    @Operation(summary = "刷新jwt")
    @PostMapping("/refresh-token")
    public Result<String> refreshToken(){
        String uuid = BaseContext.getCurrentId();
        if(uuid == null) throw new UserNotLoginException(MessageConstant.USER_NOT_LOGIN);
        User user = (User) redisTemplate.opsForValue().get(uuid);
        if(user == null) throw new RedisAccessException(MessageConstant.REDIS_ACCESS_ERROR);

        Map<String, Object> claims = new HashMap<>();
        String id = JwtClaimsConstant.getId(user.getIdentity());
        claims.put(id, user.getId());

        String token = JwtUtil.createJwt(
                getKey(user.getIdentity()),
                getTtl(user.getIdentity()),
                claims);

        user.setToken(token);
        redisTemplate.opsForValue().set(uuid, user, getTtl(user.getIdentity()), TimeUnit.MILLISECONDS);

        return Result.success(token);
    }

    private String getKey(UserIdentity userIdentity){
        return switch (userIdentity) {
            case ADMIN -> jwtProperties.getAdminSecretKey();
            case CUSTOMER -> jwtProperties.getCustomerSecretKey();
            case EMPLOYEE -> jwtProperties.getEmployeeSecretKey();
            case MERCHANT -> jwtProperties.getMerchantSecretKey();
            default -> throw new UnknownIdentityException(MessageConstant.UNKNOWN_IDENTITY);
        };
    }

    private long getTtl(UserIdentity userIdentity){
        return switch (userIdentity) {
            case ADMIN -> jwtProperties.getAdminTtl();
            case CUSTOMER -> jwtProperties.getCustomerTtl();
            case EMPLOYEE -> jwtProperties.getEmployeeTtl();
            case MERCHANT -> jwtProperties.getMerchantTtl();
            default -> throw new UnknownIdentityException(MessageConstant.UNKNOWN_IDENTITY);
        };
    }
}
