package com.mo.service.aspectj;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

@Slf4j
@Aspect
@Component
public class UuidAutoFillAspect {

    @Pointcut("execution(* com.mo.service.impl.*.save*(..)) && @within(com.mo.service.annotation.AutoFillUuid)")
    public void autoFillSave() {}

    @Before("autoFillSave()")
    public void beforeSave(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for(Object arg : args){
            try{
                Method setUuid = arg.getClass().getMethod("setUuid", String.class);

                String newUuid = UUID.randomUUID().toString();

                setUuid.invoke(arg, newUuid);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e){
                log.warn("自动填充UUID失败,字段： {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
