package com.mo.common.enumeration;

public enum AfterSaleType {
    REFUND(0),
    REPLACE(1),
    OTHER(2);

    private final int value;

    AfterSaleType(int value) {
        this.value = value;
    }

    public static AfterSaleType getByValue(int value) {
        for (AfterSaleType reason : AfterSaleType.values()) {
            if (reason.value == value) {
                return reason;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
