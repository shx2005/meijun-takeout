package com.mo.web.controller;

import com.mo.api.dto.AfterSaleDTO;
import com.mo.api.service.AfterSaleService;
import com.mo.common.enumeration.AfterSaleStatus;
import com.mo.common.result.Result;
import com.mo.entity.AfterSale;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "保存售后请求")
    @Parameters({
            @Parameter(name = "afterSaleDTO", description = "售后参数", required = true, schema = @Schema(implementation = AfterSaleDTO.class))
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/save")
    public Result<String> saveAfterSale(AfterSaleDTO afterSaleDTO) {
        AfterSale afterSale = new AfterSale();
        BeanUtils.copyProperties(afterSaleDTO, afterSale);
        afterSaleService.saveAfterSale(afterSale);
        return Result.success();
    }

    @Operation(summary = "更新售后请求")
    @Parameters({
            @Parameter(name = "afterSaleDTO", description = "售后参数", required = true, schema = @Schema(implementation = AfterSaleDTO.class))
    })
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/update/{requestId}")
    public Result<String> updateAfterSale(AfterSaleDTO afterSaleDTO, @PathVariable Long requestId) {
        AfterSale afterSale = new AfterSale();
        BeanUtils.copyProperties(afterSaleDTO, afterSale);
        afterSale.setOrderId(requestId);
        afterSaleService.updateAfterSale(afterSale);
        return Result.success();
    }

    @Operation(summary = "删除售后请求")
    @Parameter(name = "requestId", description = "售后请求id", required = true, schema = @Schema(implementation = Long.class))
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/delete/{requestId}")
    public Result<String> deleteAfterSale(@PathVariable Long requestId) {
        afterSaleService.deleteAfterSale(requestId);
        return Result.success();
    }

    @Operation(summary = "获取售后请求状态")
    @Parameter(name = "requestId", description = "售后请求id", required = true, schema = @Schema(implementation = Long.class))
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = AfterSaleStatus.class)))
    @PostMapping("/status/{requestId}")
    public Result<AfterSaleStatus> getAfterSaleStatus(@PathVariable Long requestId) {
        return Result.success(afterSaleService.getAfterSaleById(requestId).getStatus());
    }
}
