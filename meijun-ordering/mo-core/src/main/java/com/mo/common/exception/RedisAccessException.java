package com.mo.common.exception;

public class RedisAccessException extends RuntimeException {
    public RedisAccessException() {
    }
    public RedisAccessException(String message) {
        super(message);
    }
}
