package com.converage.controller.admin;

import com.converage.architecture.dto.Result;
import com.converage.entity.sys.Department;
import com.converage.architecture.utils.ResultUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

@RestController
@RequestMapping("admin/department")
public class AdminDepartmentController {


    @RequestMapping("info")
    public Result<?> viewDepartment() throws Exception {
        Department department = new Department();
        department.setId("dcf61ff4a58b4b49804acd9d21be6a0b");
        department.setDepartmentName("13");
        department.setCharger("11");
        department.setCreateBy("1");
        department.setCreateTime(new Timestamp(System.currentTimeMillis()));
        return ResultUtils.success(null,"success");
    }

}
