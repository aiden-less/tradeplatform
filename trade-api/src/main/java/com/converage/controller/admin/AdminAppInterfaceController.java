package com.converage.controller.admin;

import com.alibaba.fastjson.JSON;
import com.converage.architecture.dto.Result;
import com.converage.architecture.utils.ResultUtils;
import com.converage.entity.work.ApiTreeNode;
import com.converage.entity.work.AppInterface;
import com.converage.entity.work.Project;
import com.converage.service.work.AppInterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@RequestMapping("admin/appInterface")
public class AdminAppInterfaceController {

    @Autowired
    private AppInterfaceService appInterfaceService;

    @RequestMapping("create")
    public Result<?> createInterface(String dataStr) {
        AppInterface appInterface = JSON.parseObject(dataStr, AppInterface.class);
        appInterfaceService.createInterface(appInterface);
        return ResultUtils.success(null, "Create Success");
    }

    @RequestMapping("interfaces")
    public Result<?> queryInterfaces(Project project) throws Exception {
        ApiTreeNode apiTreeNode = appInterfaceService.queryInterfacesByProject(project);
        return ResultUtils.success(apiTreeNode, "Query Success");
    }

    @RequestMapping("update")
    public Result<?> queryInterfaces(String dataStr) throws SQLException {
        AppInterface appInterface = JSON.parseObject(dataStr, AppInterface.class);
        appInterfaceService.updateInterfaceById(appInterface);
        return ResultUtils.success(null, "Update Success");
    }

    @RequestMapping("interfaceInfo")
    public Result<?> queryInterfaceInfo(AppInterface appInterfaceVo) {
        AppInterface appInterface = appInterfaceService.queryInterfaceById(appInterfaceVo);
        return ResultUtils.success(appInterface, "Query Success");
    }
}
