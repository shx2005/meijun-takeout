package com.mo.web.controller;

import com.mo.api.service.PromotionService;
import com.mo.common.result.Result;
import com.mo.entity.Promotion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/promotions")
public class PromotionController {
    @Autowired
    private PromotionService promotionService;

    public Result<List<Promotion>> getPromotion(){
        List<Promotion> list = promotionService.getPromotion();

        return Result.success(list);
    }
}
