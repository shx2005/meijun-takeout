package com.mo.web.handler;

import com.mo.common.exception.BaseException;
import com.mo.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public Result handleException(BaseException exception){
        log.error("BaseException:{}", exception.getMessage());
        return Result.error(exception.getMessage());
    }
}
