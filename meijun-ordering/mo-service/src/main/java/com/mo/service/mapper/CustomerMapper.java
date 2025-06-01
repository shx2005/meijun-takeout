package com.mo.service.mapper;

import com.mo.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerMapper {
    Customer getCustomerByUsername(@Param("username") String username);

    Customer getCustomerByUuid(@Param("uuid") String uuid);

    void saveCustomer(@Param("customer") Customer customer);

    void updateCustomer(Customer customer);

    Customer getCustomerById(Long id);

    List<Customer> getAllCustomer();
}
