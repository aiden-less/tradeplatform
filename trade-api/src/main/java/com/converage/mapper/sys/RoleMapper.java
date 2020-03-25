package com.converage.mapper.sys;

import com.converage.entity.sys.Role;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleMapper {

    Integer deleteFunction(@Param("roleId") String roleId);

    Role listFunction(@Param("roleId") String roleId);
}
