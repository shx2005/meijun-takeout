package com.mo.api.service;

import com.mo.api.dto.CustomerInfoDTO;
import com.mo.api.dto.EmployeeInfoDTO;
import com.mo.api.dto.WithdrawMessageDTO;
import com.mo.api.vo.CustomerInfoVO;
import com.mo.api.vo.EmployeeInfoVO;
import com.mo.api.vo.SendMessageVO;
import com.mo.api.vo.WithdrawMessageVO;
import com.mo.entity.Customer;
import com.mo.entity.Message;

public interface SupportService {
    SendMessageVO sendMessage(Message message);
    WithdrawMessageVO withdrawMessage(WithdrawMessageDTO dto);

    EmployeeInfoVO getEmployeeInfo(EmployeeInfoDTO dto);
    CustomerInfoVO getCustomerInfo(CustomerInfoDTO dto);
}
