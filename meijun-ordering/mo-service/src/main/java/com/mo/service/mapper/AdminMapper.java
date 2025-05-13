package com.mo.service.mapper;

import com.mo.entity.Admin;
import com.mo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper {
    Admin getAdminByUsername(@Param("username") String username);
    Admin getAdminByUuid(@Param("uuid") String uuid);
}
