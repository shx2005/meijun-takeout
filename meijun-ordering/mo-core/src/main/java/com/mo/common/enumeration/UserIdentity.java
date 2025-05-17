package com.mo.common.enumeration;

public enum UserIdentity {
    ADMIN(1),
    MERCHANT(2),
    EMPLOYEE(3),
    CUSTOMER(4);

    private final int value;

    UserIdentity(int value) {
        this.value = value;
    }

    public static UserIdentity fromValue(int value) {
        for (UserIdentity identity : values()) {
            if (identity.value == value){
                return identity;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
