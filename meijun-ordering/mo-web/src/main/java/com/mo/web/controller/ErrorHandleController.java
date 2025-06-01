package com.mo.web.controller;

import com.mo.api.dto.ErrorHandleDTO;
import com.mo.api.vo.ErrorHandleVO;
import com.mo.api.service.ErrorHandleService;
import com.mo.common.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@Tag(name = "支付异常处理")
public class ErrorHandleController {
    @Autowired
    private ErrorHandleService errorHandleService;

    @PostMapping("/error")
    public Result<ErrorHandleVO> handle(@RequestBody ErrorHandleDTO dto) {
        return Result.success(errorHandleService.handle(dto));
    }
}
