package com.converage.controller.admin.req;

import com.converage.architecture.dto.Pagination;
import lombok.Data;

@Data
public class RoleReq {
    public String roleName;
    public Pagination pagination;
}
