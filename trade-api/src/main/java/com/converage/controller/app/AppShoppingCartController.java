package com.converage.controller.app;

import com.converage.architecture.dto.Result;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.utils.JwtUtils;
import com.converage.architecture.utils.ResultUtils;
import com.converage.constance.ShopConst;
import com.converage.service.shop.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("app/shoppingCart")
public class AppShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 操作购物车
     *
     * @param request
     * @param operatorType
     * @param spuId
     * @param shoppingCartId
     * @param specIdStr
     * @param number
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("operator/{operatorType}")
    public Result<?> operator(HttpServletRequest request, @PathVariable("operatorType") Integer operatorType, String spuId, String shoppingCartId, String specIdStr, Integer number) throws UnsupportedEncodingException {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        Object o = null;
        switch (operatorType) {
            case ShopConst.OPERATOR_TYPE_INSERT:
                shoppingCartService.createShoppingCartItem(userId, spuId, specIdStr, number);
                o = "加入购物车成功";
                break;

            case ShopConst.OPERATOR_TYPE_DELETE:
                shoppingCartService.deleteShoppingCartItem(userId, shoppingCartId);
                break;

            case ShopConst.OPERATOR_TYPE_UPDATE:
                shoppingCartService.updateNumber(userId, shoppingCartId, number);
                break;

            case ShopConst.OPERATOR_TYPE_QUERY_LIST:
                o = shoppingCartService.queryShoppingCartItem(userId);
                break;

            default:
                throw new BusinessException("错误操作类型");
        }
        return ResultUtils.success(o);
    }


    /**
     * 统计购物车商品数量
     *
     * @param request
     * @return
     */
    @RequestMapping("countGoods")
    public Result<?> updateNumber(HttpServletRequest request) {
        String userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        return ResultUtils.success(shoppingCartService.countShoppingCartGoods(userId));
    }
}
