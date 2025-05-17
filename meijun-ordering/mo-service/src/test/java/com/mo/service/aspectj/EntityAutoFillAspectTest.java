package com.mo.service.aspectj;

import com.mo.service.annotation.AutoFillTime;
import com.mo.common.context.BaseContext;
import com.mo.entity.Dish;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class EntityAutoFillAspectUnitTest {

    @InjectMocks
    private EntityAutoFillAspect aspect;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private Method method;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBeforeSave_AutoFillTimeFields() throws Exception {
        // 构造一个被 AutoFillTime 注解修饰的类
        Class<?> mockClass = MockAnnotatedClass.class;

        // 创建一个目标对象
        Object target = new Dish();

        // 设置 JoinPoint 的返回值
        when(joinPoint.getArgs()).thenReturn(new Object[]{target});

        // 设置 BaseContext 当前线程用户 ID
        BaseContext.setCurrentId("user-123");

        // 执行切面逻辑
        aspect.beforeSave(joinPoint);

        // 验证字段是否填充
        assertNotNull(((Dish) target).getCreateTime());
        assertNotNull(((Dish) target).getUpdateTime());
        assertEquals("user-123", ((Dish) target).getCreateUser());
        assertEquals("user-123", ((Dish) target).getUpdateUser());

        // 清除线程变量
        BaseContext.removeCurrentId();
    }

    @Test
    void testBeforeUpdate_OnlyUpdateTimeFilled() throws Exception {
        // 构造一个被 AutoFillTime 注解修饰的类
        Class<?> mockClass = MockAnnotatedClass.class;

        // 创建一个目标对象
        Object target = new Dish();

        // 设置 JoinPoint 的返回值
        when(joinPoint.getArgs()).thenReturn(new Object[]{target});

        // 设置 BaseContext 当前线程用户 ID
        BaseContext.setCurrentId("user-123");

        // 执行切面逻辑
        aspect.beforeUpdate(joinPoint);

        // 验证只有 updateTime 填充了
        assertNull(((Dish) target).getCreateTime()); // 不填充 create_time
        assertNotNull(((Dish) target).getUpdateTime());
        assertEquals("user-123", ((Dish) target).getUpdateUser());

        // 清除线程变量
        BaseContext.removeCurrentId();
    }

    // 模拟一个被 @AutoFillTime 注解修饰的类
    @AutoFillTime
    static class MockAnnotatedClass {}
}
