package com.mo.common.enumeration;

public enum OrderPayStaus {
    UNPAID(0),
    PAID(1),
    REFUND(2);

    private final int value;

    OrderPayStaus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static OrderPayStaus fromValue(int value) {
        for (OrderPayStaus status : OrderPayStaus.values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }
}
