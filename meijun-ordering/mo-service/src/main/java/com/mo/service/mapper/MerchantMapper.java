package com.mo.service.mapper;

import com.mo.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MerchantMapper {

    Merchant getMerchantByUsername(@Param("username") String username);
    Merchant getMerchantByUuid(@Param("uuid") String uuid);
    Merchant getMerchantById(@Param("merchantId") Long merchantId);

    void saveMerchant(@Param("merchant") Merchant merchant);

    void updateMerchant(Merchant merchant);
}
