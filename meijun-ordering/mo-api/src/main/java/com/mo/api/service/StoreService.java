package com.mo.api.service;

import com.mo.entity.Store;

import java.util.List;

public interface StoreService {
    List<Store> getStore();

    void updateStore(Store store);
}
