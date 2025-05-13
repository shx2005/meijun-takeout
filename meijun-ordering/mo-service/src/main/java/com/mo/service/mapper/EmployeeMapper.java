package com.mo.service.mapper;

import com.mo.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {
    Employee getEmployeeByUsername(@Param("username") String username);
    Employee getEmployeeByUuid(@Param("uuid") String uuid);

    void addEmployee(@Param("employee") Employee employee);
}
