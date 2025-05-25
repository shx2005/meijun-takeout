package com.mo.web.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.common.enumeration.UserIdentity;
import com.mo.entity.*;

import java.util.Map;

public class UserDeserializer {

    public static User deserializer(Map<String, Object> map, ObjectMapper mapper){
        String identityStr = (String) map.get("identity");
        UserIdentity identity = UserIdentity.valueOf(identityStr);

        Class<? extends User> userType = switch (identity) {
            case CUSTOMER -> Customer.class;
            case ADMIN -> Admin.class;
            case EMPLOYEE -> Employee.class;
            case MERCHANT -> Merchant.class;
            default -> throw new IllegalArgumentException("Unknown user identity: " + identity);
        };

        return mapper.convertValue(map, userType);
    }
}
