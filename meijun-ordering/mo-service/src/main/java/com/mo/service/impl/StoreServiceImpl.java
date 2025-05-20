package com.mo.service.impl;

import com.mo.entity.Store;
import com.mo.service.mapper.StoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreServiceImpl {
    @Autowired
    private StoreMapper storeMapper;

    public List<Store> getStore() {
        return storeMapper.getAll();
    }

    public void updateStore(Store store) {
        storeMapper.updateStore(store);
    }
}
