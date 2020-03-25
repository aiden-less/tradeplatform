package com.converage.service.shop;

import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.utils.ValueCheckUtils;
import com.converage.constance.CommonConst;
import com.converage.constance.ShopConst;
import com.converage.entity.shop.ShoppingAddress;
import com.converage.mapper.shop.ShoppingAddressMapper;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Pattern;

@Service
public class ShoppingAddressService extends BaseService {

    @Autowired
    private ShoppingAddressMapper shoppingAddressMapper;

    /**
     * 设置默认收货地址
     *
     * @param addressId 收货地址id
     * @return
     */
    public Integer updateDefaultAddress(String addressId, String userId) {
        Integer i;
        i = shoppingAddressMapper.updateDefault(addressId, userId);
        i = shoppingAddressMapper.updateNotDefault(addressId, userId);
        return i;
    }

    /**
     * 操作收货地址
     *
     * @param userId          用户id
     * @param operatorType    操作类型
     * @param shoppingAddress 收货地址实体
     * @return
     */
    public Object operatorAddress(String userId, Integer operatorType, ShoppingAddress shoppingAddress) {
        String REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
        Object o = null;
        Class clazz = ShoppingAddress.class;
        Integer i;
        String shoperAddressId = shoppingAddress.getShoperAddressId();
        shoppingAddress.setId(shoperAddressId);
        switch (operatorType) {
            case ShopConst.SHOPPING_ADDRESS_OPERATOR_TYPE_INSERT: //添加
                if (!Pattern.matches(REGEX_MOBILE, shoppingAddress.getShoperTel())) {
                    throw new BusinessException("号码不属于手机号");
                }
                shoppingAddress.setUserId(userId);
                i = insertIfNotNull(shoppingAddress);
                if (shoppingAddress.getIfDefault() != null && shoppingAddress.getIfDefault()) {
                    updateDefaultAddress(shoppingAddress.getId(), userId);
                }
                if (i == 0) {
                    throw new BusinessException("添加收货地址失败");
                }
                break;

            case ShopConst.SHOPPING_ADDRESS_OPERATOR_TYPE_DELETE: //删除
                ValueCheckUtils.notEmpty(shoperAddressId, "收货地址id不能为空");
                i = delete(shoppingAddress);
                if (i == 0) {
                    throw new BusinessException("删除收货地址失败");
                }
                break;

            case ShopConst.SHOPPING_ADDRESS_OPERATOR_TYPE_UPDATE: //编辑
                ValueCheckUtils.notEmpty(shoperAddressId, "收货地址id不能为空");
                if (!Pattern.matches(REGEX_MOBILE, shoppingAddress.getShoperTel())) {
                    throw new BusinessException("号码不属于手机号");
                }
                i = updateIfNotNull(shoppingAddress);

                if (shoppingAddress.getIfDefault()) {
                    updateDefaultAddress(shoperAddressId, userId);
                }

                if (i == 0) {
                    throw new BusinessException("保存收货地址失败");
                }
                break;

            case ShopConst.SHOPPING_ADDRESS_OPERATOR_TYPE_QUERY: //查询
                Map orderMap = ImmutableMap.of(ShoppingAddress.If_default, CommonConst.MYSQL_DESC);
                o = selectListByWhereString(ShoppingAddress.User_id + "=", userId, null, clazz, orderMap);
                break;

            case ShopConst.SHOPPING_ADDRESS_OPERATOR_TYPE_DEFAULT: //设置默认
                ValueCheckUtils.notEmpty(shoperAddressId, "收货地址id不能为空");
                i = updateDefaultAddress(shoperAddressId, userId);
                if (i == 0) {
                    o = "设置收货地址失败";
                }
                break;
        }
        return o;
    }

    /**
     * 查询用户默认收获地址
     *
     * @param userId
     * @return
     */
    public ShoppingAddress getDefaultShoppingAddress(String userId) {
        Map<String, Object> whereMap = ImmutableMap.of(ShoppingAddress.User_id + "=", userId, ShoppingAddress.If_default + "=", true);
        return selectOneByWhereMap(whereMap, ShoppingAddress.class);
    }

}
