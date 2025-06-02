package com.mo.service.impl;

import com.mo.api.dto.ErrorHandleDTO;
import com.mo.api.service.ErrorHandleService;
import com.mo.api.vo.ErrorHandleVO;
import org.springframework.stereotype.Service;

@Service
public class ErrorHandleServiceImpl implements ErrorHandleService {
    public ErrorHandleVO handle(ErrorHandleDTO dto) {
        return ErrorHandleVO.builder()
                .retryToken("")
                .redirectUrl("")
                .orderId(dto.getOrderId())
                .payType(dto.getPayType())
                .build();
    }
}
