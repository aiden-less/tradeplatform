package com.converage.controller.app;

import com.converage.architecture.dto.Result;
import com.converage.architecture.utils.JwtUtils;
import com.converage.architecture.utils.ResultUtils;
import com.converage.entity.shop.ShoppingAddress;
import com.converage.service.shop.ShoppingAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("app/shoppingAddress")
public class AppShoppingAddressController {
    @Autowired
    private ShoppingAddressService shoppingAddressService;

    /**
     * 操作收货地址
     *
     * @param request
     * @param operatorType    收货地址操作类型 ShopConst.SHOPPING_ADDRESS_OPERATOR_TYPE_*
     * @param shoppingAddress 操作类型实体
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("operator/{operatorType}")
    public Result<?> operator(HttpServletRequest request, @PathVariable("operatorType") Integer operatorType, ShoppingAddress shoppingAddress) throws UnsupportedEncodingException {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        Object o = shoppingAddressService.operatorAddress(userId, operatorType, shoppingAddress);
        return ResultUtils.success(o);
    }

    /**
     * 获取默认收获地址
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("default")
    public Result<?> getDefaultAdd(HttpServletRequest request) throws UnsupportedEncodingException {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        return ResultUtils.success(shoppingAddressService.getDefaultShoppingAddress(userId));
    }

}
