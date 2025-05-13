package com.mo.service.mapper;

import com.mo.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MerchantMapper {
    Merchant getMerchantByUsername(@Param("username") String username);
    Merchant getMerchantByUuid(@Param("uuid") String uuid);

    void addMerchant(@Param("merchant") Merchant merchant);
}
