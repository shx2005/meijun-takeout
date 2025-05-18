package com.mo.common.enumeration;

public enum OrderStatus {
    PENDING(0),
    COMPLETED(1),
    CANCELED(2),
    UNPAID(3),
    PAID(4);

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
