package com.sky.mapper;

import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);


    /**
     * 新增员工
     * @param employee
     *  @return
     * **/
    @Insert("insert into employee (username, name, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user) values (#{username}, #{name}, #{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void save(Employee employee);

    /**
     * 分页查询
     * @param employeePageQueryDTO page,pageSize,name   page=1,pageSize=10,name=null
     * @return
     */

    List<Employee> page(EmployeePageQueryDTO employeePageQueryDTO);


    long pagetotalCount(EmployeePageQueryDTO employeePageQueryDTO);
}
