package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        password=DigestUtils.md5DigestAsHex(password.getBytes());
//        System.out.println("md5加密后:"+password);
        //
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }


    public void save(EmployeeDTO employeeDTO){

        Employee employee = new Employee();

        //对象属性拷贝，将DTO中的数据拷贝到Entity中,前提是属性名相同
        BeanUtils.copyProperties(employeeDTO, employee);

        //设置默认密码123456，并md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        //设置状态，1正常，0锁定
        employee.setStatus(StatusConstant.ENABLE);
        //设置创建/更新时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //设置创建/更新人  先默认只有一个管理员，所以创建/更新人都是1(管理员id为1)
        //TODO
        employee.setUpdateUser(BaseContext.getCurrentId());
        employee.setCreateUser(BaseContext.getCurrentId());

        employeeMapper.save(employee);
    }

    @Override
    public PageResult page(EmployeePageQueryDTO employeePageQueryDTO) {
        PageResult pageResult=new PageResult();  //page=1,pageSize=10,name=null
        //设置limit 格式
        employeePageQueryDTO.setPage((employeePageQueryDTO.getPage()-1)*employeePageQueryDTO.getPageSize());
        long total=employeeMapper.pagetotalCount(employeePageQueryDTO);
        pageResult.setTotal(total);
        pageResult.setRecords(employeeMapper.page(employeePageQueryDTO));
        return pageResult;
    }

}
