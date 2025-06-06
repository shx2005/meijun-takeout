package com.mo.common.enumeration;

public enum PayMethod {
    ali_pay(0),
    wx_pay(1),
    cash_pay(2);

    private final int value;

    PayMethod(int value) {
        this.value = value;
    }

    public static PayMethod getByValue(int value) {
        for (PayMethod method : values()) {
            if (method.value == value) {
                return method;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
