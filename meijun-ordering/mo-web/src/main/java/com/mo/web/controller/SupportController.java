package com.mo.web.controller;

import com.mo.api.dto.CustomerServiceInfoDTO;
import com.mo.api.dto.SendMessageDTO;
import com.mo.api.dto.WithdrawMessageDTO;
import com.mo.api.service.SupportService;
import com.mo.api.vo.CustomerServiceInfoVO;
import com.mo.api.vo.SendMessageVO;
import com.mo.api.vo.WithdrawMessageVO;
import com.mo.common.enumeration.MessageStaus;
import com.mo.common.result.Result;
import com.mo.entity.Message;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/support")
@Tag(name = "支持管理")
public class SupportController {

    private final SupportService supportService;

    public SupportController(SupportService supportService) {
        this.supportService = supportService;
    }

    @PostMapping("/customer/messages")
    public Result<SendMessageVO> sendMessageFromCustomer(SendMessageDTO dto){
        Message message = new Message();
        BeanUtils.copyProperties(dto, message);
        message.setStaus(MessageStaus.unread);

        SendMessageVO vo = supportService.sendMessage(message);

        return Result.success(vo);
    }

    @PostMapping("/customer/info")
    public Result<CustomerServiceInfoVO> getCustomerServiceInfo(CustomerServiceInfoDTO dto){
        return Result.success(supportService.getCustomerServiceInfo(dto));
    }

    @DeleteMapping("customer/messages")
    public Result<WithdrawMessageVO> withdrawMessage(WithdrawMessageDTO dto){
        return Result.success(supportService.withdrawMessage(dto));
    }
}
