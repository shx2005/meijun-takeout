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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "顾客发送信息")
    @Parameters({
            @Parameter(name = "dto", description = "发送信息DTO", schema = @Schema(implementation = SendMessageDTO.class))
    })
    @ApiResponse (responseCode = "200", description = "成功", content =  @Content(schema = @Schema(implementation = SendMessageVO.class)))
    @PostMapping("/customer/messages")
    public Result<SendMessageVO> sendMessageFromCustomer(@RequestBody SendMessageDTO dto){
        Message message = new Message();
        BeanUtils.copyProperties(dto, message);
        message.setStaus(MessageStaus.unread);
        message.setSenderType(0);

        SendMessageVO vo = supportService.sendMessage(message);

        return Result.success(vo);
    }

    @Operation (summary = "获取客服信息")
    @Parameters({
            @Parameter(name = "dto", description = "获取客服信息DTO", schema = @Schema(implementation = EmployeeInfoDTO.class))
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = EmployeeInfoVO.class)))
    @GetMapping("/customer/info")
    public Result<EmployeeInfoVO> getCustomerServiceInfo(@RequestBody EmployeeInfoDTO dto){

        return Result.success(supportService.getEmployeeInfo(dto));
    }

    @Operation(summary = "撤回顾客消息")
    @Parameters({
            @Parameter( name = "dto", description = "撤回信息DTO", schema = @Schema( implementation = WithdrawMessageDTO.class))
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = WithdrawMessageVO.class)))
    @DeleteMapping("customer/messages")
    public Result<WithdrawMessageVO> withdrawMessageFromCustomer(@RequestBody WithdrawMessageDTO dto){
        return Result.success(supportService.withdrawMessage(dto));
    }

    @Operation(summary = "店员撤发送信息")
    @Parameters({
            @Parameter(name = "dto", description = "发送信息DTO", required = true, schema = @Schema(implementation = SendMessageDTO.class))
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = SendMessageVO.class)))
    @PostMapping("/employee/messages")
    public Result<SendMessageVO> sendMessageFromEmployee(@RequestBody SendMessageDTO dto){
        Message message = new Message();
        BeanUtils.copyProperties(dto, message);
        message.setStaus(MessageStaus.unread);
        message.setSenderType(1);

        SendMessageVO vo = supportService.sendMessage(message);

        return Result.success(vo);
    }

    @Operation (summary = "获取顾客信息")
    @Parameters({
            @Parameter(name = "dto", description = "获取用户信息DTO", schema = @Schema(implementation = CustomerInfoDTO.class))
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = CustomerInfoVO.class)))
    @GetMapping("/employee/info")
    public Result<CustomerInfoVO> getCustomerServiceInfo(@RequestBody CustomerInfoDTO dto){
        CustomerInfoVO vo = supportService.getCustomerInfo(dto);

        return Result.success(vo);
    }

    @Operation(summary = "店员撤回信息")
    @Parameters({
             @Parameter(name = "dto", description = "撤回信息参数", required = true, schema = @Schema(implementation = WithdrawMessageDTO.class))
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = WithdrawMessageVO.class)))
    @DeleteMapping("employee/messages")
    public Result<WithdrawMessageVO> withdrawMessageFromEmployee(@RequestBody WithdrawMessageDTO dto){
        WithdrawMessageVO vo = supportService.withdrawMessage(dto);

        return Result.success(vo);
    }
}
