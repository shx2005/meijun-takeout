package com.mo.web.controller;

import com.mo.api.dto.AuthLoginDTO;
import com.mo.api.dto.AuthRegisterDTO;
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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
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

    @PostMapping("/login")
    @Tag(name = "登录")
    @Parameters({
            @Parameter(name = "authLoginDTO", description = "登录信息", required = true)
    })
    public Result<AuthLoginVo> login(@RequestBody AuthLoginDTO authLoginDTO) {
        log.info("login:{}", authLoginDTO);

        User user = authService.login(authLoginDTO);

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

    @PostMapping("/register")
    public Result<User> register(@RequestBody AuthRegisterDTO authRegisterDTO) {
        log.info("register:{}", authRegisterDTO);

        User user = new User();
        BeanUtils.copyProperties(authRegisterDTO, user);
        authService.saveUser(user);

        return Result.success(user);
    }

    @PostMapping("/logout")
    public Result<String> logout(){
        String uuid = BaseContext.getCurrentId();
        log.info("logout: {}", uuid);
        BaseContext.removeCurrentId();
        redisTemplate.delete(uuid);
        return Result.success();
    }

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
