package com.mo.service.mapper;

import com.mo.entity.Store;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StoreMapper {
    List<Store> getAll();

    void updateStore(Store store);

}
