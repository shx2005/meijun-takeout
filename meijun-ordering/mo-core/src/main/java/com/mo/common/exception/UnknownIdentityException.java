package com.mo.common.exception;

public class UnknownIdentityException extends RuntimeException {
    public UnknownIdentityException() {
    }
    public UnknownIdentityException(String message) {
        super(message);
    }
}
