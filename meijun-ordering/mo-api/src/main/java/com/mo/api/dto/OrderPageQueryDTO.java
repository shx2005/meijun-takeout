package com.mo.api.dto;

import lombok.Data;
import org.springframework.format.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrderPageQueryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int page;

    private int size;

    private Integer status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private String userUid;

}
