package com.mo.service.mapper;

import com.mo.entity.Promotion;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PromotionMapper {
    List<Promotion> getPromotion();
}
