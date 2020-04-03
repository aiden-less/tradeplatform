package com.converage.controller.app;

import com.alibaba.fastjson.JSONObject;
import com.converage.architecture.dto.Pagination;
import com.converage.architecture.dto.Result;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.utils.JwtUtils;
import com.converage.architecture.utils.ResultUtils;
import com.converage.service.common.RSAService;
import com.converage.entity.user.*;
import com.converage.service.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

import static com.converage.constance.CommonConst.REQUST_TIME_OUT_SECOND;

@RestController
@RequestMapping("app/user")
public class AppUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserSendService userSendService;

    @Autowired
    private CertificationService certificationService;

    @Autowired
    private UserMessageService userMessageService;

    @Autowired
    private RSAService rsaService;


    @RequestMapping("msg/send")
    public Result<?> sendMsg4register(HttpServletRequest request, User userReq) throws BusinessException, UnsupportedEncodingException {
        String msgCode = userSendService.createMsgRecord(request, userReq);
//        return ResultUtils.success("发送短信成功：" + msgCode);
        return ResultUtils.success("发送短信成功");
    }

    @RequestMapping("register")
    public Result<?> register(String desKeyStr, String paramStr) throws Exception {
        User userReq = JSONObject.parseObject(rsaService.decryptParam(desKeyStr, paramStr), User.class);
        return ResultUtils.success(userService.createUser(userReq, true), "注册成功");
    }

    @RequestMapping("register4H5")
    public Result<?> register1(User userReq) throws Exception {
        return ResultUtils.success(userService.createUser(userReq, true), "注册成功");
    }

//    @RequestMapping("login")
//    public Result<?> loginByPhone(String desKeyStr, String paramStr) throws Exception {
//        User userReq = JSONObject.parseObject(rsaService.decryptParam(desKeyStr, paramStr), User.class);
//        rsaService.checkRequestTimeout(userReq, REQUST_TIME_OUT_SECOND);
//        User userInfo = userService.loginByPhone(userReq);
//        return ResultUtils.success(userInfo, "登录成功");
//    }

    /**
     * 用户登录（账号密码登录）
     *
     * @return
     */
    @RequestMapping("login")
    public Result<?> loginByUserAccount(String desKeyStr, String paramStr) throws Exception {
        User userReq = JSONObject.parseObject(rsaService.decryptParam(desKeyStr, paramStr), User.class);
        User userInfo = userService.loginByUserAccount(userReq);
        return ResultUtils.success(userInfo, "登录成功");
    }


    @RequestMapping("info")
    public Result<?> assets(HttpServletRequest request) throws UnsupportedEncodingException {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        return ResultUtils.success();
    }


    @RequestMapping("cert")
    public Result<?> cert(HttpServletRequest request, String realName, String licenseNumber, MultipartFile frontFiles, MultipartFile backFiles, MultipartFile handleFiles) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        certificationService.createCert(userId, realName, licenseNumber, frontFiles, backFiles, handleFiles);
        return ResultUtils.success("申请实名认证成功");
    }


    @RequestMapping("message/detail")
    public Result<?> messageDetail(HttpServletRequest request, String messageId, Integer msgType, Pagination pagination) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        return ResultUtils.success(userMessageService.getUserMessage(userId, messageId));
    }

    @RequestMapping("cert/info")
    public Result<?> certInfo(HttpServletRequest request) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        return ResultUtils.success(certificationService.getCertificationByUserId(userId));
    }

}
