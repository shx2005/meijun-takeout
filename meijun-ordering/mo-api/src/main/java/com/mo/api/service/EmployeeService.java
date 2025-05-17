package com.mo.api.service;

import com.mo.entity.Employee;

import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployee();

    List<Employee> getEmployeePage(int offset, int size);
}
