package com.mo.api.service;

import com.mo.api.dto.ErrorHandleDTO;
import com.mo.api.vo.ErrorHandleVO;

public interface ErrorHandleService {
    ErrorHandleVO handle(ErrorHandleDTO dto);
}
