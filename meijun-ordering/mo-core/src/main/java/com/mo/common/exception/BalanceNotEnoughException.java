package com.mo.common.exception;

public class BalanceNotEnoughException extends RuntimeException {
    public BalanceNotEnoughException() {
        super();
    }

    public BalanceNotEnoughException(String message) {
        super(message);
    }
}
