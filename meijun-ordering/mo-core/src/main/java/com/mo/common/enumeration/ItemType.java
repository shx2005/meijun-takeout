package com.mo.common.enumeration;

public enum ItemType {
    DISH(0),
    SET_MEAL(1);

    private final int value;

    ItemType(int value) {
        this.value = value;
    }

    public static ItemType fromValue(int value) {
        for (ItemType itemType : ItemType.values()) {
            if (itemType.getValue() == value) {
                return itemType;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
