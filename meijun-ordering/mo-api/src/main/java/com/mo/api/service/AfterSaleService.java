package com.mo.api.service;

import com.mo.entity.AfterSale;
import org.springframework.stereotype.Service;

public interface AfterSaleService {
    void saveAfterSale(AfterSale afterSale);
    int updateAfterSale(AfterSale afterSale);
    int deleteAfterSale(Long id);
    AfterSale getAfterSaleById(Long id);
}
