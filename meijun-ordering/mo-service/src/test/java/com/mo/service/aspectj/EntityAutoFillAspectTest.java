package com.mo.service.aspectj;

import com.mo.service.annotation.AutoFillTime;
import com.mo.common.context.BaseContext;
import com.mo.entity.Dish;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class EntityAutoFillAspectUnitTest {

    private EntityAutoFillAspect aspect;

    @Mock
    private JoinPoint joinPoint;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        aspect = new EntityAutoFillAspect();
    }

    // 假设 @AutoFillTime 注解移到了测试方法上，添加示例注解
    @AutoFillTime
    @Test
    void testBeforeSave_AutoFillTimeFields() {
        Dish dish = new Dish();
        when(joinPoint.getArgs()).thenReturn(new Object[]{dish});
        BaseContext.setCurrentId("user-123");

        aspect.beforeSave(joinPoint);

        assertNotNull(dish.getCreateTime());
        assertNotNull(dish.getUpdateTime());
        assertEquals("user-123", dish.getCreateUser());
        assertEquals("user-123", dish.getUpdateUser());

        BaseContext.removeCurrentId();
    }

    // 假设 @AutoFillTime 注解移到了测试方法上，添加示例注解
    @AutoFillTime
    @Test
    void testBeforeUpdate_OnlyUpdateTimeFilled() {
        Dish dish = new Dish();
        when(joinPoint.getArgs()).thenReturn(new Object[]{dish});
        BaseContext.setCurrentId("user-123");

        aspect.beforeUpdate(joinPoint);

        assertNull(dish.getCreateTime());
        assertNotNull(dish.getUpdateTime());
        assertEquals("user-123", dish.getUpdateUser());

        BaseContext.removeCurrentId();
    }

// 移除不再需要的类
// static class MockAnnotatedClass {}
}
