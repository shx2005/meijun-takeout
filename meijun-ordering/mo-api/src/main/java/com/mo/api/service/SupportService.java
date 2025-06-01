package com.mo.api.service;

import com.mo.api.dto.CustomerServiceInfoDTO;
import com.mo.api.dto.WithdrawMessageDTO;
import com.mo.api.vo.CustomerServiceInfoVO;
import com.mo.api.vo.SendMessageVO;
import com.mo.api.vo.WithdrawMessageVO;
import com.mo.entity.Message;

public interface SupportService {
    SendMessageVO sendMessage(Message message);
    CustomerServiceInfoVO getCustomerServiceInfo(CustomerServiceInfoDTO dto);
    WithdrawMessageVO withdrawMessage(WithdrawMessageDTO dto);
}
