package com.mo.web.controller;

import com.mo.api.service.RedisService;
import com.mo.common.constant.RedisKeyConstant;
import com.mo.web.MoWebApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = MoWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    @Autowired
    private UserController userController;
    @Autowired
    private RedisService redisService;

    @Test
    public void testInfo() throws ClassNotFoundException {
        String uuid = "123456789";
        Long id = 1L;
        redisService.hSet(RedisKeyConstant.USER_ID, uuid, id);

        Object obj = redisService.hGet(RedisKeyConstant.USER_ID, uuid);
        Long userId = null;
        if(obj instanceof Number){
            userId = ((Number) obj).longValue();
        }
        assertEquals(id, userId);
    }
}
