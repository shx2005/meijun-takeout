package com.mo.service.impl;

import com.mo.api.service.StoreService;
import com.mo.entity.Store;
import com.mo.service.mapper.StoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {
    @Autowired
    private StoreMapper storeMapper;

    @Override
    public List<Store> getStore() {
        return storeMapper.getAll();
    }

    @Override
    public void updateStore(Store store) {
        storeMapper.updateStore(store);
    }
}
