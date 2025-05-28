package com.mo.service.impl;

import com.mo.api.service.UserService;
import com.mo.common.constant.MessageConstant;
import com.mo.common.context.BaseContext;
import com.mo.common.exception.AccountNotFoundException;
import com.mo.common.exception.UserBussinessException;
import com.mo.entity.*;
import com.mo.service.annotation.AutoFillTime;
import com.mo.service.mapper.AdminMapper;
import com.mo.service.mapper.CustomerMapper;
import com.mo.service.mapper.EmployeeMapper;
import com.mo.service.mapper.MerchantMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Override
    public User info(String uuid) {
        User user = adminMapper.getAdminByUuid(uuid);
        if(user == null) user = merchantMapper.getMerchantByUuid(uuid);
        if(user == null) user = employeeMapper.getEmployeeByUuid(uuid);
        if(user == null) user = customerMapper.getCustomerByUuid(uuid);

        if(user == null) throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);

        return user;
    }

    @Override
    public void update(User user){
        switch (user.getIdentity()){
            case ADMIN -> {
                Admin admin = Admin.fromUser(user);
                adminMapper.updateAdmin(admin);
            }
            case MERCHANT -> {
                Merchant  merchant = Merchant.fromUser(user);
                merchantMapper.updateMerchant(merchant);
            }
            case EMPLOYEE -> {
                Employee employee = Employee.fromUser(user);
                employeeMapper.updateEmployee(employee);
            }
            case CUSTOMER -> {
                Customer customer = Customer.fromUser(user);
                customerMapper.updateCustomer(customer);
            }
            default -> throw new UserBussinessException(MessageConstant.UNKNOWN_IDENTITY);
        }
    }
}