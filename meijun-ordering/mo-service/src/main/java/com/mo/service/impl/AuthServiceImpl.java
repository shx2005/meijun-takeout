package com.mo.service.impl;

import com.mo.common.exception.UnknownIdentityException;
import com.mo.service.annotation.AutoFillTime;
import com.mo.api.dto.AuthLoginDTO;
import com.mo.api.dto.AuthRegisterDTO;
import com.mo.api.service.AuthService;
import com.mo.common.constant.MessageConstant;
import com.mo.common.enumeration.UserIdentity;
import com.mo.common.exception.AccountNotFoundException;
import com.mo.common.exception.PasswordErrorException;
import com.mo.common.exception.RegisterFailedException;
import com.mo.entity.Customer;
import com.mo.entity.Employee;
import com.mo.entity.Merchant;
import com.mo.entity.User;
import com.mo.service.annotation.AutoFillUuid;
import com.mo.service.mapper.AdminMapper;
import com.mo.service.mapper.CustomerMapper;
import com.mo.service.mapper.EmployeeMapper;
import com.mo.service.mapper.MerchantMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public User login(AuthLoginDTO authLoginDTO){
        String username = authLoginDTO.getUsername();
        String password = authLoginDTO.getPassword();
        UserIdentity identity = authLoginDTO.getIdentity();
        User user = null;

        switch(identity){
            case ADMIN -> {
                user = adminMapper.getAdminByUsername(username);
            }
            case MERCHANT -> {
                user = merchantMapper.getMerchantByUsername(username);
            }
            case EMPLOYEE -> {
                user = employeeMapper.getEmployeeByUsername(username);
            }
            case CUSTOMER -> {
                user = customerMapper.getCustomerByUsername(username);
            }
            default -> throw new UnknownIdentityException(MessageConstant.UNKNOWN_IDENTITY);
        }

        // 检查用户是否存在
        if (user == null) {
            throw new AccountNotFoundException("账号不存在: " + username);
        }

        //todo 密码加密
        if(!user.getPassword().equals(password)) throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);

        return user;
    }

    @Override
    @AutoFillTime
    @AutoFillUuid
    public void saveUser(User user) {
        UserIdentity identity = user.getIdentity();
        String username = user.getUsername();

        //todo 密码加密
        switch (identity){
            case ADMIN -> throw new RegisterFailedException("暂不支持管理员注册");
            case MERCHANT -> {
                Merchant mer = merchantMapper.getMerchantByUsername(username);
                if(mer != null) throw new RegisterFailedException("商家已存在");

                Merchant merchant = Merchant.fromUser(user);
                merchant.setAddress(user.getAddress());
                merchantMapper.saveMerchant(merchant);
                log.info("商家{}注册成功", merchant.getUsername());
            }
            case EMPLOYEE -> {
                Employee emp = employeeMapper.getEmployeeByUsername(username);
                if(emp != null) throw new RegisterFailedException("员工已存在");

                Employee employee = Employee.fromUser(user);
                Merchant merchant = merchantMapper.getMerchantById(user.getMerchantId());
                if(merchant == null) throw new RegisterFailedException("没有对应商家");
                Long id = merchant.getId();
                employee.setMerchant_id(id);
                employeeMapper.saveEmployee(employee);
                log.info("员工{}注册成功", employee.getUsername());
            }
            case CUSTOMER -> {
                Customer cus = customerMapper.getCustomerByUsername(username);
                if(cus != null) throw new RegisterFailedException("用户已存在");

                Customer customer = Customer.fromUser(user);
                customer.setBalance(BigDecimal.valueOf(0));
                customerMapper.saveCustomer(customer);
                log.info("用户{}注册成功", customer.getUsername());
            }
            default -> throw new RegisterFailedException("未知身份");
        }
    }
}
