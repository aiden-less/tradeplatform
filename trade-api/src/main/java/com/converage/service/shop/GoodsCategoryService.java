package com.converage.service.shop;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.converage.architecture.dto.Pagination;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.constance.CommonConst;
import com.converage.utils.ValueCheckUtils;
import com.converage.constance.ShopConst;
import com.converage.entity.shop.GoodsCategory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GoodsCategoryService extends BaseService {
    /**
     * 新增类目
     *
     * @param goodsCategory
     */
    public Integer save(GoodsCategory goodsCategory) {
        ValueCheckUtils.notEmpty(goodsCategory.getCategoryName(), "类目不能为空");
        String entityId = goodsCategory.getId();
        if (StringUtils.isNoneBlank(entityId)) {
            ValueCheckUtils.notEmpty(selectOneById(entityId, GoodsCategory.class), "未找到类目记录");
            return updateIfNotNull(goodsCategory);
        } else {
            return insertIfNotNull(goodsCategory);
        }
    }

    /**
     * 类目列表
     *
     * @param goodsCategory 商铺
     * @param pagination    分页对象
     * @return
     */
    public List<GoodsCategory> list(GoodsCategory goodsCategory, Pagination pagination) {
        Map<String, Object> whereMap = Maps.newHashMapWithExpectedSize(2);
        whereMap.put(GoodsCategory.If_valid + "=", true);
        if (goodsCategory != null) {
            String categoryName = goodsCategory.getCategoryName();
            if (StringUtils.isNoneBlank(categoryName)) {
                whereMap.put(GoodsCategory.Category_name + " LIKE ", "%" + categoryName + "%");
            }
            String id = goodsCategory.getId();
            if (id != null) {
                whereMap.put(GoodsCategory.Id + " != ", id);
            }
        }

        return selectListByWhereMap(whereMap, pagination, GoodsCategory.class, ImmutableMap.of(GoodsCategory.Sort, CommonConst.MYSQL_DESC));
    }


    /**
     * 类目详情
     *
     * @param entityId 实体id
     * @return
     */
    public GoodsCategory detail(String entityId) {
        return selectOneById(entityId, GoodsCategory.class);
    }


    /**
     * 类目操作
     *
     * @param goodsCategory
     * @param operatorType
     * @return
     */
    public Object operatorCategory(GoodsCategory goodsCategory, Integer operatorType) {
        Object o = null;
        switch (operatorType) {
            case ShopConst.OPERATOR_TYPE_INSERT: //添加
                o = "保存类目成功";
                if (save(goodsCategory) == 0) {
                    throw new BusinessException("保存类目失败");
                }
                break;

            case ShopConst.OPERATOR_TYPE_DELETE: //删除
                goodsCategory.setIfValid(false);
                o = "删除类目成功";
                if (updateIfNotNull(goodsCategory) == 0) {
                    throw new BusinessException("删除类目失败");
                }
                break;

            case ShopConst.OPERATOR_TYPE_QUERY_LIST: //查询列表
                o = list(goodsCategory, goodsCategory.getPagination());
                break;

            case ShopConst.OPERATOR_TYPE_QUERY_DETAIL: //详情
                o = detail(goodsCategory.getId());
                break;
        }
        return o;
    }
}
