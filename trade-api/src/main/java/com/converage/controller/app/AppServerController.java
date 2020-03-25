package com.converage.controller.app;

import com.converage.architecture.utils.ResultUtils;
import com.converage.service.common.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RequestMapping(value = "/app/server")
@RestController
public class AppServerController {

    @Autowired
    private ServerService serverService;

    @RequestMapping("info")
    public Object payType() throws UnsupportedEncodingException {
        return ResultUtils.success(serverService.serverInfo());
    }
}
