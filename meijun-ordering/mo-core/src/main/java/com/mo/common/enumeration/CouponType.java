package com.mo.common.enumeration;

public enum CouponType {
    fixed(0),
    percentage(1);

    private final int value;

    CouponType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CouponType getByValue(int value) {
        for (CouponType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
