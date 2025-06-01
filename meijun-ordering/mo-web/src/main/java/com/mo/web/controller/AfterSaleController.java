package com.mo.web.controller;

import com.mo.api.dto.AfterSaleDTO;
import com.mo.api.service.AfterSaleService;
import com.mo.common.enumeration.AfterSaleStatus;
import com.mo.common.result.Result;
import com.mo.entity.AfterSale;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/after-sale")
@Tag(name = "操作售后请求")
public class AfterSaleController {
    @Autowired
    private AfterSaleService afterSaleService;

    @PostMapping("/save")
    public Result<String> saveAfterSale(AfterSaleDTO afterSaleDTO) {
        AfterSale afterSale = new AfterSale();
        BeanUtils.copyProperties(afterSaleDTO, afterSale);
        afterSaleService.saveAfterSale(afterSale);
        return Result.success();
    }

    @PostMapping("/update/{requestId}")
    public Result<String> updateAfterSale(AfterSaleDTO afterSaleDTO, @PathVariable Long requestId) {
        AfterSale afterSale = new AfterSale();
        BeanUtils.copyProperties(afterSaleDTO, afterSale);
        afterSale.setOrderId(requestId);
        afterSaleService.updateAfterSale(afterSale);
        return Result.success();
    }

    @PostMapping("/delete/{requestId}")
    public Result<String> deleteAfterSale(@PathVariable Long requestId) {
        afterSaleService.deleteAfterSale(requestId);
        return Result.success();
    }

    @PostMapping("/status/{requestId}")
    public Result<AfterSaleStatus> getAfterSaleStatus(@PathVariable Long requestId) {
        return Result.success(afterSaleService.getAfterSaleById(requestId).getStatus());
    }
}
