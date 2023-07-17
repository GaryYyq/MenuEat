package com.garyyyq.menueats.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.garyyyq.menueats.common.R;
import com.garyyyq.menueats.entity.Employee;
import com.garyyyq.menueats.entity.Setmeal;
import com.garyyyq.menueats.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/employee")
public class EmployeeController {

//    @Autowired
//    private EmployeeService employeeService;


    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Employee login
     * @param request
     * @param employee
     * @return
     */
    @PostMapping(value = "/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());

        Employee emp = employeeService.getOne(queryWrapper);

        if(emp == null){
            return R.error("Username does not exist");
        }

        if(!emp.getPassword().equals(password)){
            return R.error("Password is incorrect");
        }


        if(emp.getStatus() == 0){
            return R.error("Account is disabled");
        }

        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * Employee logout
     * @param request
     * @return
     */
    @PostMapping(value = "/logout")
    public R<String> logout(HttpServletRequest request){

        //Clear session
        request.getSession().removeAttribute("employee");
        return R.success("Logout successfully");
    }


    /**
     * Employee registration
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        log.info("add employee {}", employee);

        //Set default password
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        //get employee id
//        Long empId = (Long) request.getSession().getAttribute("employee");
//
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("Add employee successfully");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        log.info("page is {}, pageSize is {}, name is {}", page, pageSize, name);

        //Create page object
        Page pageInfo = new Page(page, pageSize);

        //Create query wrapper
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

        //Add filter conditions
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);

        //Sort by update time
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //Call page method
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }


    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        log.info("update employee: {}", employee);

        long id = Thread.currentThread().getId();
        log.info("Thread id in employee update: {}", id);

//        Long empId = (Long) request.getSession().getAttribute("employee");
//        log.info(empId.toString());
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);

        return R.success("Update employee successfully");

    }

    //Search employee info by id
    @GetMapping(value = "/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("get employee by ids: {}", id);
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("Employee does not exist");
    }


}
