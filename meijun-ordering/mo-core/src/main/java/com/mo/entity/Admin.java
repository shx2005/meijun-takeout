package com.mo.entity;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.mo.common.enumeration.UserIdentity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import lombok.Builder.Default;

@Schema(name = "管理员信息")
@EqualsAndHashCode(callSuper = true)
@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Admin extends User{
    @Serial
    private static final long serialVersionUID = 1L;
    @Builder.Default
    private UserIdentity identity = UserIdentity.ADMIN;
    @Builder.Default
    private Role role = Role.admin;

    public static Admin fromUser(User user){
        Admin admin = new Admin();
        BeanUtils.copyProperties(user, admin);
        admin.setIdentity(UserIdentity.ADMIN);
        admin.setRole(Role.admin);

        return admin;
    }
}

enum Role{
    root,
    admin
};