package com.converage.controller.admin;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.dto.Result;
import com.converage.controller.admin.req.RoleReq;
import com.converage.entity.sys.Role;
import com.converage.architecture.utils.ResultUtils;
import com.converage.service.sys.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("admin/role")
public class AdminRoleController {

    @Autowired
    private RoleService roleService;

    @RequestMapping("create")
    public Result<?> createRole(@RequestBody Role role) {
        roleService.insertRole(role);
        return ResultUtils.success("编辑角色成功");
    }

    @RequestMapping("list")
    public Result<?> roleList(@RequestBody RoleReq roleReq) {
        Pagination pagination = roleReq.getPagination();
        List<Role> roleList;
        if (StringUtils.isNoneBlank(roleReq.getRoleName())) {
            roleList = roleService.selectListByWhereString(Role.Role_name + " like ", "%" + roleReq.getRoleName() + "%", pagination, Role.class);
        } else {
            roleList = roleService.selectAll(pagination, Role.class);
        }

        Integer count = pagination == null ? roleList.size() : pagination.getTotalRecordNumber();
        return ResultUtils.success(roleList, count);
    }

    @RequestMapping("edit")
    public Result<?> editRole(@RequestBody Role role) {
        Role role1 = roleService.listFunction(role.getId());
        return ResultUtils.success(role1);
    }

}
