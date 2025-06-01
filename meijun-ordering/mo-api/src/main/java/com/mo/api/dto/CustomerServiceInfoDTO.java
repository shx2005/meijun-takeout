package com.mo.api.dto;

import com.mo.api.service.SupportService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceInfoDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    private Long employeeId;
}
