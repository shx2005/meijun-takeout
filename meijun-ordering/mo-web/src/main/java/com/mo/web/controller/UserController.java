package com.mo.web.controller;

import com.mo.api.service.UserService;
import com.mo.common.context.BaseContext;
import com.mo.common.result.Result;
import com.mo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/info")
    public Result<User> info(){
        String uuid = BaseContext.getCurrentId();
        if(uuid == null) {
            return Result.error("用户未登录");
        }

        User user = (User) redisTemplate.opsForValue().get(uuid);
        if(user == null) {
            // 如果Redis中没有，尝试从数据库获取
            try {
                user = userService.info(uuid);
                // 如果找到了用户，将其放入Redis
                if(user != null) {
                    redisTemplate.opsForValue().set(uuid, user);
                }
            } catch (Exception e) {
                return Result.error("获取用户信息失败: " + e.getMessage());
            }
        }
        
        if(user == null) {
            return Result.error("用户不存在");
        }
        
        return Result.success(user);
    }
}
