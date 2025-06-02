package com.mo.common.enumeration;

public enum MessageStaus {
    unread(0),
    read(1);

    private int value;

    MessageStaus(int value) {
        this.value = value;
    }

    public static MessageStaus getByValue(int value) {
        for (MessageStaus item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
