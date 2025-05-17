package com.mo.service.mapper;

import com.mo.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmployeeMapper {
    Employee getEmployeeByUsername(@Param("username") String username);
    Employee getEmployeeByUuid(@Param("uuid") String uuid);

    void saveEmployee(@Param("employee") Employee employee);
}
