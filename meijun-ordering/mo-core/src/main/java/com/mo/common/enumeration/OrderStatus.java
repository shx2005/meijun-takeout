package com.mo.common.enumeration;

public enum OrderStatus {
    PENDING(1),
    COMPLETED(2),
    CANCELED(3),
    UNPAID(4),
    PAID(5);

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
