package com.mo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Store implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    Long id;
    Long merchantId;
    String name;
}
