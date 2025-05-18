package com.mo.api.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 员工传输对象
 */
@Data
public class EmployeeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String password;
    private String name;
    private String phoneNum;
    private String gender;
    private String idNumber;
    private Long merchantId;

}
