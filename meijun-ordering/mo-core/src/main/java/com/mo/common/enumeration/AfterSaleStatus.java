package com.mo.common.enumeration;

public enum AfterSaleStatus {
    PENDING(0),
    APPROVED(1),
    REJECTED(2),
    COMPLETED(3);

    private final int value;

    AfterSaleStatus(int value) {
        this.value = value;
    }

    public static AfterSaleStatus fromValue(int value) {
        for (AfterSaleStatus status : values()) {
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
