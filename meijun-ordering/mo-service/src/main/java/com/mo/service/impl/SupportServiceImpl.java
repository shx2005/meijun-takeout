package com.mo.service.impl;

import com.mo.api.dto.CustomerInfoDTO;
import com.mo.api.dto.EmployeeInfoDTO;
import com.mo.api.dto.WithdrawMessageDTO;
import com.mo.api.service.SupportService;
import com.mo.api.vo.CustomerInfoVO;
import com.mo.api.vo.EmployeeInfoVO;
import com.mo.api.vo.SendMessageVO;
import com.mo.api.vo.WithdrawMessageVO;
import com.mo.common.constant.SendMessageConstant;
import com.mo.entity.Customer;
import com.mo.entity.Employee;
import com.mo.entity.Message;
import com.mo.service.mapper.CustomerMapper;
import com.mo.service.mapper.EmployeeMapper;
import com.mo.service.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SupportServiceImpl implements SupportService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public SendMessageVO sendMessage(Message message) {
        messageMapper.saveMessage(message);

        return SendMessageVO.builder()
                .id(message.getId())
                .status(SendMessageConstant.OK)
                .build();
    }

    @Override
    public WithdrawMessageVO withdrawMessage(WithdrawMessageDTO dto){
        String status = String.valueOf(messageMapper.deleteMessage(dto.getMessageId()) > 0);

        return WithdrawMessageVO.builder()
                .messageId(dto.getMessageId())
                .status(status)
                .build();
    }

    @Override
    public EmployeeInfoVO getEmployeeInfo(EmployeeInfoDTO dto){
        Employee employee = employeeMapper.getEmployeeById(dto.getEmployeeId());

        return EmployeeInfoVO.builder()
                .employeeId(employee.getId())
                .employeePhone(employee.getPhoneNum())
                .employAvatar(employee.getAvatar_url())
                .build();
    }

    @Override
    public CustomerInfoVO getCustomerInfo(CustomerInfoDTO dto) {
        Customer customer = customerMapper.getCustomerById(dto.getCustomerId());

        return CustomerInfoVO.builder()
                .CustomerId(dto.getCustomerId())
                .CustomerName(customer.getName())
                .CustomerAvatar(customer.getAvatar_url())
                .build();
    }
}
