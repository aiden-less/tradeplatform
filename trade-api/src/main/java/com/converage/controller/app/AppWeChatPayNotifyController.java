package com.converage.controller.app;

import com.converage.architecture.utils.ResultUtils;
import com.converage.service.shop.OrderWechatPayNotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.converage.constance.ShopConst.WeChatPayNotifyController;
import static com.converage.constance.ShopConst.WeChatPayShopOrderNotify;

@RestController
@RequestMapping("app" + WeChatPayNotifyController)
public class AppWeChatPayNotifyController {

    @Autowired
    private OrderWechatPayNotifyService orderWechatPayNotifyService;

    @RequestMapping(WeChatPayShopOrderNotify)
    @ResponseBody
    public String shopOrderNotify(HttpServletRequest request) throws IOException {
        orderWechatPayNotifyService.shoppingNotify(request.getInputStream());
        return ResultUtils.weChatPaySuccess();
    }


}
