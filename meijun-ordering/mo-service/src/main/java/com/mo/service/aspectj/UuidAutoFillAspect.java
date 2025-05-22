package com.mo.service.aspectj;

// import com.mo.common.utils.IdGenerator; // 移除未使用的导入
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID; // 确保这个导入存在，因为 UUID 被使用了

@Aspect
@Component
@Slf4j
public class UuidAutoFillAspect {

    @Pointcut("execution(* com.mo.service.impl.*.*(..)) && @annotation(com.mo.service.annotation.AutoFillUuid)")
    public void autoFill() {}

    @Before("autoFill()")
    public void before(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        log.info("[before] 开始：{}", joinPoint.getSignature());
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            Method setUuid = arg.getClass().getMethod("setUuid", String.class);
            String uuid = UUID.randomUUID().toString().replace("-", "");
            setUuid.invoke(arg, uuid);
            log.info("成功为 {} 填充了uuid：{}", arg.getClass().getSimpleName(), uuid);
        }
    }
}
