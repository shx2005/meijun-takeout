package com.mo.api.service;

import com.mo.entity.Admin;

import java.util.List;

public interface AdminService {
    List<Admin> getAllAdmins();

    List<Admin> getPage(int offset, int size);

    void updateAdmin(Admin admin);

    void delete(Long id);

    void saveAdmin(Admin admin);
}
