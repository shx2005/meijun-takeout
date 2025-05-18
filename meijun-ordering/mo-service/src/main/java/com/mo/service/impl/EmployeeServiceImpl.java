package com.mo.service.impl;

import com.mo.api.service.EmployeeService;
import com.mo.entity.Employee;
import com.mo.service.annotation.AutoFillTime;
import com.mo.service.annotation.AutoFillUuid;
import com.mo.service.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AutoFillUuid
@AutoFillTime
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    EmployeeMapper employeeMapper;

    @Override
    public List<Employee> getAllEmployee(){
        return employeeMapper.getAll();
    }

    @Override
    public List<Employee> getEmployeePage(int offset, int size){
        return employeeMapper.getPage(offset, size);
    }

    @Override
    public void saveEmployee(Employee employee){
        employeeMapper.saveEmployee(employee);
    }

    @Override
    public void updateEmployee(Employee employee){
        employeeMapper.updateEmployee(employee);
    }

    @Override
    public void deleteEmployee(Long id){
        employeeMapper.deleteEmployee(id);
    }
}
