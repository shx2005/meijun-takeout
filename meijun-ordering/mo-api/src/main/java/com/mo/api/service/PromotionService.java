package com.mo.api.service;

import com.mo.entity.Promotion;

import java.util.List;

public interface PromotionService {
    List<Promotion> getPromotion();
    void savePromotion(Promotion promotion);
    void deletePromotion(Long promotionId);
    void updatePromotion(Promotion promotion);
}
