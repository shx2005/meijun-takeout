package com.mo.common.constant;

import com.mo.common.enumeration.UserIdentity;

public class JwtClaimsConstant {
    public static final String ADMIN_ID = "adminID";
    public static final String CUSTOMER_ID = "customerID";
    public static final String EMPLOYEE_ID = "employeeID";
    public static final String MERCHANT_ID = "merchantID";

    public static final String ADMIN = "admin";
    public static final String CUSTOMER = "customer";
    public static final String EMPLOYEE = "employee";
    public static final String MERCHANT = "merchant";

    public static String getName(UserIdentity userIdentity){
        return switch (userIdentity) {
            case ADMIN -> ADMIN;
            case CUSTOMER -> CUSTOMER;
            case EMPLOYEE -> EMPLOYEE;
            case MERCHANT -> MERCHANT;
            default -> null;
        };
    }

    public static String getId(UserIdentity userIdentity){
        return switch (userIdentity) {
            case ADMIN -> ADMIN_ID;
            case CUSTOMER -> CUSTOMER_ID;
            case EMPLOYEE -> EMPLOYEE_ID;
            case MERCHANT -> MERCHANT_ID;
            default -> null;
        };
    }
}
