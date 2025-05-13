package com.mo.service.mapper;

import com.mo.entity.Customer;
import com.mo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CustomerMapper {
    Customer getCustomerByUsername(@Param("username") String username);
    Customer getCustomerByUuid(@Param("uuid") String uuid);

    void addCustomer(@Param("customer") Customer customer);
}
