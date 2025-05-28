package com.mo.common.enumeration;

public enum OrderPayStaus {
    unpaid(0),
    paid(1),
    refund(2);

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
