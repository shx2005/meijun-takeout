package com.mo.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.api.dto.MpLoginDTO;
import com.mo.common.exception.*;
import com.mo.common.properties.MpProperties;
import com.mo.service.annotation.AutoFillTime;
import com.mo.api.dto.AuthLoginDTO;
import com.mo.api.service.AuthService;
import com.mo.common.constant.MessageConstant;
import com.mo.common.enumeration.UserIdentity;
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
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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
    @Autowired
    private MpProperties mpProperties;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Override
    public User login(AuthLoginDTO authLoginDTO){
        String username = authLoginDTO.getUsername();
        String password = authLoginDTO.getPassword();
        UserIdentity identity = authLoginDTO.getIdentity();
        User user;

        if (identity == null) {
            throw new AuthBusinessException(MessageConstant.UNKNOWN_IDENTITY);
        }

        switch(identity){
            case ADMIN -> user = adminMapper.getAdminByUsername(username);
            case MERCHANT -> user = merchantMapper.getMerchantByUsername(username);
            case EMPLOYEE -> user = employeeMapper.getEmployeeByUsername(username);
            case CUSTOMER -> user = customerMapper.getCustomerByUsername(username);

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
    public User mpLogin(MpLoginDTO dto){
        String openid = getOpenid(dto.getCode());

        if(openid == null || openid.isEmpty()){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        Customer customer = customerMapper.getCustomerByOpenid(openid);
        if(customer == null){
            customer = Customer.builder()
                    .identity(UserIdentity.CUSTOMER)
                    .username("default")
                    .password("123456")
                    .balance(BigDecimal.ZERO)
                    .openid(openid)
                    .build();
            customerMapper.saveCustomer(customer);
        }
        return customer;
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

    private String getOpenid(String code){
        Map<String, String> params = new HashMap<>();
        params.put("appid", mpProperties.getAppid());
        params.put("secret", mpProperties.getSecret());
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");
        RestTemplate restTemplate = new RestTemplate();

        String response = restTemplate.getForObject(WX_LOGIN, String.class, params);
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return jsonNode.get("openid").asText();
    }
}
