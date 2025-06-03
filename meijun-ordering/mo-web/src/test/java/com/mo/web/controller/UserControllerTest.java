package com.mo.web.controller;

import com.mo.api.service.DeliveryService;
import com.mo.api.service.RedisService;
import com.mo.common.constant.RedisKeyConstant;
import com.mo.web.MoWebApplication;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = MoWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    @Autowired
    private UserController userController;
    @Autowired
    private RedisService redisService;
    @MockBean
    private DeliveryService deliveryService;

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

    @Test
    public void testDistance(){
        Mockito.when(deliveryService.calculateDistance(Mockito.anyString(), Mockito.anyString())).thenReturn(3917.0);

        String userLocation = "116.466485,39.995197";
        String merchantLocation = "116.46424,40.020642";
        int distance = (int) deliveryService.calculateDistance(userLocation, merchantLocation);

        assertEquals(3917, distance);
    }
}
