package com.mo.service.impl;

import com.mo.api.dto.CustomerServiceInfoDTO;
import com.mo.api.dto.WithdrawMessageDTO;
import com.mo.api.service.SupportService;
import com.mo.api.vo.CustomerServiceInfoVO;
import com.mo.api.vo.SendMessageVO;
import com.mo.api.vo.WithdrawMessageVO;
import com.mo.common.constant.SendMessageConstant;
import com.mo.entity.Employee;
import com.mo.entity.Message;
import com.mo.service.mapper.EmployeeMapper;
import com.mo.service.mapper.MessageMapper;
import org.springframework.stereotype.Service;

@Service
public class SupportServiceImpl implements SupportService {

    private final MessageMapper messageMapper;
    private final EmployeeMapper employeeMapper;

    public SupportServiceImpl(MessageMapper messageMapper, EmployeeMapper employeeMapper) {
        this.messageMapper = messageMapper;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public SendMessageVO sendMessage(Message message) {
        messageMapper.saveMessage(message);

        return SendMessageVO.builder()
                .id(message.getId())
                .status(SendMessageConstant.OK)
                .build();
    }

    @Override
    public CustomerServiceInfoVO getCustomerServiceInfo(CustomerServiceInfoDTO dto){
        Employee employee = employeeMapper.getEmployeeById(dto.getEmployeeId());
        return CustomerServiceInfoVO.builder()
                .employeeId(employee.getId())
                .employeePhone(employee.getPhoneNum())
                .employAvatar(employee.getAvatar_url())
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
}
