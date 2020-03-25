package com.converage.controller.app;

import com.converage.architecture.dto.Result;
import com.converage.architecture.utils.JwtUtils;
import com.converage.architecture.utils.ResultUtils;
import com.converage.service.transaction.LctService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 旺旺 on 2020/3/20.
 */
@RestController
@RequestMapping("app/lct") //法币交易
public class AppLctController {

    @Autowired
    private LctService lctService;


    // 创建订单
    @RequestMapping("order/create")
    public Result<?> createOrder(HttpServletRequest request, String desKeyStr, String paramStr) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();


        return ResultUtils.success();
    }

}
