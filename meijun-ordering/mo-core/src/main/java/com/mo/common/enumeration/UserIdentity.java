package com.mo.common.enumeration;

public enum UserIdentity {
    ADMIN(0, "Admin"),
    MERCHANT(1, "Merchant"),
    EMPLOYEE(2, "Employee"),
    CUSTOMER(3, "Customer");

    private final int value;
    private final String name;

    UserIdentity(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static UserIdentity fromValue(int value) {
        for (UserIdentity identity : values()) {
            if (identity.value == value){
                return identity;
            }
        }
        return null;
    }

    public static UserIdentity fromString(String identity) {
        for (UserIdentity userIdentity : values()) {
            if (userIdentity.name().equalsIgnoreCase(identity)) {
                return userIdentity;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
