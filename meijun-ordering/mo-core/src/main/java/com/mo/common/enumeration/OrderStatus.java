package com.mo.common.enumeration;

public enum OrderStatus {
    pending(0),
    unconfirmed(1),
    confirmed(2),
    delivering(3),
    completed(4),
    cancelled(5);

    private final int value;

    OrderStatus(int value) {
        this.value = value;
    }

    public static OrderStatus fromValue(int value) {
        for (OrderStatus status : values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
