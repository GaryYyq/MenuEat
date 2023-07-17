package com.garyyyq.menueats.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.garyyyq.menueats.entity.Employee;
import com.garyyyq.menueats.mapper.EmployeeMapper;
import com.garyyyq.menueats.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
