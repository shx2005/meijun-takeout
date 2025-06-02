package com.mo.service.aspectj;

import com.mo.common.context.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 自动填充createUser, updateUser
 * @author Anesidora
 */
@Slf4j
@Aspect
@Component
public class UserAutoFillAspect {

    @Pointcut("execution(* com.mo.service.impl.*.save*(..)) && @annotation(com.mo.service.annotation.AutoFillUser)")
    public void autoFillSave() {}

    @Pointcut("execution(* com.mo.service.impl.*.update*(..)) && @annotation(com.mo.service.annotation.AutoFillUser)")
    public void autoFillUpdate() {}

    @Before("autoFillSave()")
    public void beforeSave(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        log.info("[before] 开始：{}", joinPoint.getSignature());
        for(Object arg : args){
            try{
                Method setCreateUser = arg.getClass().getMethod("setCreateUser", String.class);
                Method setUpdateUser = arg.getClass().getMethod("setUpdateUser", String.class);
                Method getUsername = arg.getClass().getMethod("getUsername");

                String username = (String) getUsername.invoke(arg);
                setCreateUser.invoke(arg, username);
                setUpdateUser.invoke(arg, username);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e){
                log.warn("自动填充创建(更新)人失败,字段： {}", e.getMessage());
            }
        }
    }

    @Before("autoFillUpdate()")
    public void beforeUpdate(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        log.info("[Before] 开始：{}", joinPoint.getSignature());
        for(Object arg : args){
            try{
                Method setUpdateUser = arg.getClass().getMethod("setUpdateUser", String.class);
                Method getUsername = arg.getClass().getMethod("getUsername");
                String username = (String) getUsername.invoke(arg);
                setUpdateUser.invoke(arg, username);

            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e){
                log.warn("自动填充更新人失败,字段： {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }

    }
}
