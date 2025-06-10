package com.mo.common.enumeration;

public enum ActiveStatus {
    inactive(0),
    active(1);

    private final int value;

    ActiveStatus(int value) {
        this.value = value;
    }

    public static ActiveStatus getByValue(int value) {
        for (ActiveStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
