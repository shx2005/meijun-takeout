package com.mo.service.aspectj;

import com.mo.common.context.BaseContext;
import com.mo.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class EntityAutoFillAspect {

    //拦截所有 save*() 方法和 update*() 方法，并且被 AutoFillTime 注解修饰的类
    @Pointcut("execution(* com.mo.service.impl.*.save*(..)) && @within(com.mo.service.annotation.AutoFillTime)")
    public void autoFillSave(){}

    @Pointcut("execution(* com.mo.service.impl.*.update*(..)) && @within(com.mo.service.annotation.AutoFillTime)")
    public void autoFillUpdate(){}

    @Before("autoFillSave()")
    public void beforeSave(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        for(Object arg : args){
            try{
                Method setCreateTime = arg.getClass().getMethod("setCreateTime", LocalDateTime.class);
                Method setUpdateTime = arg.getClass().getMethod("setUpdateTime", LocalDateTime.class);
                LocalDateTime now = LocalDateTime.now();

                setCreateTime.invoke(arg, now);
                setUpdateTime.invoke(arg, now);
                log.info("自动填充时间成功,字段： {}", arg.getClass().getName());

                String uuid = BaseContext.getCurrentId();
                //todo 填充用户

                try{
                    Method setCreateUser = arg.getClass().getMethod("setCreateUser", String.class);
                    Method setUpdateUser = arg.getClass().getMethod("setUpdateUser", String.class);

                    setCreateUser.invoke(arg, uuid);
                    setUpdateUser.invoke(arg, uuid);
                } catch (NoSuchMethodException ignored) {

                }

            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                log.warn("保存时自动填充时间失败,字段： {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    @Before("autoFillUpdate()")
    public void beforeUpdate(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        for(Object arg : args){
            try {
                Method setUpdateTime = arg.getClass().getMethod("setUpdateTime", LocalDateTime.class);
                LocalDateTime now = LocalDateTime.now();
                setUpdateTime.invoke(arg, now);

                String uuid = BaseContext.getCurrentId();

                try{
                    Method setUpdateUser = arg.getClass().getMethod("setUpdateUser", String.class);
                    setUpdateUser.invoke(arg, uuid);
                } catch (NoSuchMethodException ignored) {

                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                log.warn("更新时自动填充时间失败,字段： {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
