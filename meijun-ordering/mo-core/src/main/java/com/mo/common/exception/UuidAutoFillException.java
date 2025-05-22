package com.mo.common.exception;

public class UuidAutoFillException extends RuntimeException {
    public UuidAutoFillException() {
    }
    public UuidAutoFillException(String message) {
        super(message);
    }
    public UuidAutoFillException(Throwable cause){
        super(cause);
    }
}
