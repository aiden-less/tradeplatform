package com.converage.architecture.web.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.converage.architecture.dto.Result;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.utils.JwtUtils;
import com.converage.architecture.utils.ResultUtils;
import com.converage.service.common.GlobalConfigService;
import com.converage.utils.EnvironmentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.converage.constance.ShopConst.AliPayShopOrderNotifyURL;
import static com.converage.constance.ShopConst.WeChatPayShopOrderNotifyURL;

@Component
public class AuthorityInterceptor implements HandlerInterceptor {

    @Autowired
    private GlobalConfigService globalConfigService;

    @Autowired
    private EnvironmentUtils environmentUtils;

    private static final List<String> excludePath = new ArrayList<>(Arrays.asList(
            "/admin/subscriber/login", "/app/user/msg/send", "/app/user/loginByPhone", "/app/user/loginByUserAccount", "/app/user/wx/synchronizing",
            "/app/user/register", "/app/user/register4H5", "/app/user/bindPhone", "/app/common/vertifyPic", "/app/user/pwd/reset",
            "/app/server/info", "/app" + WeChatPayShopOrderNotifyURL, "/app" + AliPayShopOrderNotifyURL,
            "/app/user/checkMsgCode", "/admin/user/sendAdminLoginMsg",
            "/app/assets/trace/query", "/app/assets/trace/appeal", "/app/assets/rewardQrcode/scan", "/app/assets/rewardQrcode/receive"
    ));

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String requestUrl = httpServletRequest.getRequestURI();

        if (excludePath.contains(requestUrl)) {
            if ("/admin/subscriber/login".equals(requestUrl)) {
                validIp(httpServletRequest);
            }
            return true;
        }
        String appUrl = "/app/.*";
        String adminUrl = "/admin/.*";
        String token = httpServletRequest.getHeader(JwtUtils.ACCESS_TOKEN_NAME);
        if (requestUrl.matches(appUrl)) {
            if (null != token) {
                Boolean verifyJwtResult = JwtUtils.verifyJwt4App(token);
                if (verifyJwtResult) {
                    return true;
                }
            }
        } else if (requestUrl.matches(adminUrl)) {
            if (null != token) {
                Boolean verifyJwtResult = JwtUtils.verifyJwt4Admin(token);
                if (verifyJwtResult) {
                    return true;
                }
            }
        } else {
            throw new BusinessException("error url:" + requestUrl);
        }
        Result result = ResultUtils.reLogin("re-login");
        httpServletResponse.getWriter().write(JSONObject.toJSONString(result));
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    private void validIp(HttpServletRequest httpServletRequest) {
//        if (environmentUtils.isTest() || environmentUtils.isDev()) {
//            return;
//        }
//        Boolean flag = false;
//        String[] passIp = globalConfigService.getByDb(GlobalConfigService.Enum.PASS_IP).split(",");
//        String reqIp = IPUtils.getClientIP(httpServletRequest);
//        for (String ip : passIp) {
//            if (ip.equals(reqIp)) {
//                flag = true;
//            }
//        }
//
//        if (!flag) {
//            throw new LoginException();
//        }
    }

    public static void main(String[] args) {
        String appUrl = "/app/.*";
        String adminUrl = "/admin/.*";

        System.out.println("/admin/subscrib".matches(adminUrl));
    }
}
