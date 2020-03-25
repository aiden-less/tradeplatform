package com.converage.controller.app;

import com.alibaba.fastjson.JSONObject;
import com.converage.architecture.utils.ResultUtils;
import com.converage.exception.AliPayNotifyException;
import com.converage.service.shop.OrderAliPayNotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import static com.converage.constance.ShopConst.AliPayNotifyController;
import static com.converage.constance.ShopConst.AliPayShopOrderNotify;

@RestController
@RequestMapping("app" + AliPayNotifyController)
public class AppAliPayNotifyController {

    @Autowired
    private OrderAliPayNotifyService orderAliPayNotifyService;

    @RequestMapping(AliPayShopOrderNotify)
    @ResponseBody
    public String shopOrderNotify(HttpServletRequest request) throws AliPayNotifyException {
        String mapStr = JSONObject.toJSONString(request.getParameterMap());
        Map<String, List<String>> map2 = JSONObject.parseObject(mapStr, Map.class);
        orderAliPayNotifyService.shoppingNotify(map2);
        return ResultUtils.aliPaySuccess();
    }
}
