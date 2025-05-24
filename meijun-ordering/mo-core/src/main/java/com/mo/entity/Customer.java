package com.mo.entity;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.mo.common.enumeration.UserIdentity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;

import java.io.Serial;
import java.time.LocalDateTime;

@Tag(name = "客户信息")
@EqualsAndHashCode(callSuper = true)
@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends User{
    @Serial
    private static final long serialVersionUID = 1L;
    @Builder.Default
    private UserIdentity identity = UserIdentity.CUSTOMER;
    private double balance;

    public static Customer fromUser(User user){
        Customer customer = new Customer();
        BeanUtils.copyProperties(user, customer);
        customer.setIdentity(UserIdentity.CUSTOMER);

        return customer;
    }
}
