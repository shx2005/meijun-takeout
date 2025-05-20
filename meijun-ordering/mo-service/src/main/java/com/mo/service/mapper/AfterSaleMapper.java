package com.mo.service.mapper;

import com.mo.entity.AfterSale;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AfterSaleMapper {
    public void saveAfterSale(AfterSale afterSale);
}
