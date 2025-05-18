package com.mo.common.enumeration;

public enum UserIdentity {
    ADMIN(0),
    MERCHANT(1),
    EMPLOYEE(2),
    CUSTOMER(3);

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
