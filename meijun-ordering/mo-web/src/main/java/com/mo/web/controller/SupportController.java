package com.mo.web.controller;

import com.mo.api.dto.CustomerInfoDTO;
import com.mo.api.dto.EmployeeInfoDTO;
import com.mo.api.dto.SendMessageDTO;
import com.mo.api.dto.WithdrawMessageDTO;
import com.mo.api.service.SupportService;
import com.mo.api.vo.CustomerInfoVO;
import com.mo.api.vo.EmployeeInfoVO;
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
    public Result<SendMessageVO> sendMessageFromCustomer(@RequestBody SendMessageDTO dto){
        Message message = new Message();
        BeanUtils.copyProperties(dto, message);
        message.setStaus(MessageStaus.unread);
        message.setSenderType(0);

        SendMessageVO vo = supportService.sendMessage(message);

        return Result.success(vo);
    }

    @GetMapping("/customer/info")
    public Result<EmployeeInfoVO> getCustomerServiceInfo(@RequestBody EmployeeInfoDTO dto){
        return Result.success(supportService.getEmployeeInfo(dto));
    }

    @DeleteMapping("customer/messages")
    public Result<WithdrawMessageVO> withdrawMessageFromCustomer(@RequestBody WithdrawMessageDTO dto){
        return Result.success(supportService.withdrawMessage(dto));
    }

    @PostMapping("/employee/messages")
    public Result<SendMessageVO> sendMessageFromEmployee(@RequestBody SendMessageDTO dto){
        Message message = new Message();
        BeanUtils.copyProperties(dto, message);
        message.setStaus(MessageStaus.unread);
        message.setSenderType(1);

        SendMessageVO vo = supportService.sendMessage(message);

        return Result.success(vo);
    }

    @GetMapping("/employee/info")
    public Result<CustomerInfoVO> getCustomerServiceInfo(@RequestBody CustomerInfoDTO dto){
        return Result.success(supportService.getCustomerInfo(dto));
    }

    @DeleteMapping("employee/messages")
    public Result<WithdrawMessageVO> withdrawMessageFromEmployee(@RequestBody WithdrawMessageDTO dto){
        return Result.success(supportService.withdrawMessage(dto));
    }
}
