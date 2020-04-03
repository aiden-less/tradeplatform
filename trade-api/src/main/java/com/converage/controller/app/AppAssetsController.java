package com.converage.controller.app;

import com.converage.architecture.dto.Result;
import com.converage.architecture.utils.JwtUtils;
import com.converage.architecture.utils.ResultUtils;
import com.converage.dto.AssetsFinanceQuery;
import com.converage.service.assets.UserAssetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 旺旺 on 2020/4/3.
 */
@RequestMapping(value = "app/assets")
@RestController
public class AppAssetsController {

    @Autowired
    private UserAssetsService userAssetsService;


    @RequestMapping("cct/list")
    public Result<?> cctList(HttpServletRequest request) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        return ResultUtils.success(userAssetsService.cctAssetsList(userId));
    }

    @RequestMapping("cct/detail")
    public Result<?> cctDetail(HttpServletRequest request, AssetsFinanceQuery assetsFinanceQuery) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        assetsFinanceQuery.setUserId(userId);
        return ResultUtils.success(userAssetsService.cctAssetsDetail(assetsFinanceQuery));
    }


    @RequestMapping("lct/list")
    public Result<?> lctList(HttpServletRequest request) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();

        return ResultUtils.success(userAssetsService.lctAssetsList(userId));
    }

    @RequestMapping("lct/detail")
    public Result<?> lctDetail(HttpServletRequest request, AssetsFinanceQuery assetsFinanceQuery) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        assetsFinanceQuery.setUserId(userId);
        return ResultUtils.success(userAssetsService.LctAssetsDetail(assetsFinanceQuery));
    }
}
