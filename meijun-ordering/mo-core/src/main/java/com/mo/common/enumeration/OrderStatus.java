package com.mo.common.enumeration;

public enum OrderStatus {
    PENDING(0),
    UNCOMFIRMED(1),
    CONFIRMED(2),
    DELIVERING(3),
    COMPLETED(4),
    CANCELED(5);

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
