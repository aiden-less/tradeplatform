package com.converage.service.shop;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.utils.ValueCheckUtils;
import com.converage.constance.ShopConst;
import com.converage.entity.shop.GoodsBrand;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GoodsBrandService extends BaseService {

    public Integer save(GoodsBrand goodsBrand) {
        ValueCheckUtils.notEmpty(goodsBrand.getBrandName(), "品牌名不能为空");
        String entityId = goodsBrand.getId();
        if (StringUtils.isNoneBlank(entityId)) {
            ValueCheckUtils.notEmpty(selectOneById(entityId, GoodsBrand.class), "未找到品牌记录");
            return updateIfNotNull(goodsBrand);
        } else {
            return insertIfNotNull(goodsBrand);
        }
    }


    public List list(GoodsBrand goodsBrand, Pagination pagination) {
        Class clazz = GoodsBrand.class;
        String brandName = goodsBrand.getBrandName();
        Map<String, Object> whereMap;
        if (StringUtils.isNoneBlank(brandName)) {
            whereMap = ImmutableMap.of(
                    GoodsBrand.Brand_name + " LIKE ", "%" + brandName + "%",
                    GoodsBrand.Subscriber_id + "=", goodsBrand.getSubscriberId(),
                    GoodsBrand.If_valid + "=", true);
        } else {
            whereMap = ImmutableMap.of(
                    GoodsBrand.Subscriber_id + "=", goodsBrand.getSubscriberId(),
                    GoodsBrand.If_valid + "=", true);
        }
        return selectListByWhereMap(whereMap, pagination, clazz);
    }

    public GoodsBrand detail(String entity) {
        return selectOneById(entity, GoodsBrand.class);
    }

    public Object operatorBrand(GoodsBrand goodsBrand, Integer operatorType) {
        Object o = null;
        switch (operatorType) {
            case ShopConst.OPERATOR_TYPE_INSERT: //添加
                o = "创建品牌成功";
                if (save(goodsBrand) == 0) {
                    throw new BusinessException("创建品牌失败");
                }
                break;

            case ShopConst.OPERATOR_TYPE_DELETE: //删除
                goodsBrand.setIfValid(false);
                o = "删除品牌成功";
                if (updateIfNotNull(goodsBrand) == 0) {
                    throw new BusinessException("删除品牌失败");
                }
                break;

            case ShopConst.OPERATOR_TYPE_QUERY_LIST: //查询列表
                o = list(goodsBrand, goodsBrand.getPagination());
                break;

            case ShopConst.OPERATOR_TYPE_QUERY_DETAIL: //详情
                o = detail(goodsBrand.getId());
                break;
        }
        return o;
    }
}
