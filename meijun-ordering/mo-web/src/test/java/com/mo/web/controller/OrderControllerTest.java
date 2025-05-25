package com.mo.web.controller;

import com.mo.entity.User;
import com.mo.web.MoWebApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = MoWebApplication.class)
public class OrderControllerTest {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void testGet(){
        User user = new User().builder()
                .id(1L)
                .username("admin")
                .password("123456")
                .build();
        redisTemplate.opsForValue().set("user", user);

        assert redisTemplate.opsForValue().get("user") != null;
    }
}
