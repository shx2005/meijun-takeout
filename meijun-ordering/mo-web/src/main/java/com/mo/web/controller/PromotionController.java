package com.mo.web.controller;

import com.mo.api.service.PromotionService;
import com.mo.common.result.Result;
import com.mo.entity.Promotion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "促销管理")
@Slf4j
@RestController
@RequestMapping("/api/v1/promotions")
public class PromotionController {
    @Autowired
    private PromotionService promotionService;

    @Operation(summary = "获取促销活动")
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = Promotion.class)))
    public Result<List<Promotion>> getPromotion(){
        List<Promotion> list = promotionService.getPromotion();

        return Result.success(list);
    }
}
