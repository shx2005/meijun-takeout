package com.mo.service.impl;

import com.mo.api.service.PromotionService;
import com.mo.entity.Promotion;
import com.mo.service.annotation.AutoFillTime;
import com.mo.service.mapper.PromotionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromotionServiceImpl implements PromotionService {
    @Autowired
    private PromotionMapper promotionMapper;

    @Override
    public List<Promotion> getPromotion() {
        return null;
    }

    @Override
    @AutoFillTime
    public void savePromotion(Promotion promotion) {
        promotionMapper.savePromotion(promotion);
    }

    @Override
    public void deletePromotion(Long promotionId) {
        promotionMapper.deletePromotionById(promotionId);
    }

    @Override
    @AutoFillTime
    public void updatePromotion(Promotion promotion) {
        promotionMapper.updatePromotion(promotion);
    }
}
