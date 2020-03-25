package com.converage.service.sys;

import com.converage.architecture.service.BaseService;
import com.converage.entity.sys.Role;
import com.converage.entity.sys.RoleFunction;
import com.converage.mapper.sys.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService extends BaseService {

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 删除角色接口权限
     * @param roleId
     * @return
     */
    public Integer deleteFunction(String roleId) {
        return roleMapper.deleteFunction(roleId);
    }

    /**
     * 查询角色接口权限
     * @param roleId
     * @return
     */
    public Role listFunction(String roleId) {
        return roleMapper.listFunction(roleId);
    }

    public void insertRole(Role role) {
        Role rolePo = selectOneById(role.getId(), Role.class);
        if (rolePo == null) { //新增
            insertIfNotNull(role);
        } else { //编辑
            updateIfNotNull(role);
            deleteFunction(rolePo.getId());
        }
        List<RoleFunction> rolesList = new ArrayList<>(50);
        for (String functionId : role.getFunctionIdList()) {
            rolesList.add(new RoleFunction(functionId, role.getId()));
        }
        insertBatch(rolesList, false);
    }
}
