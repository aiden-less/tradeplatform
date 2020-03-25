package com.converage.mapper.shop;

import com.converage.architecture.dto.Pagination;
import com.converage.entity.shop.OrderGoodsInfo;
import com.converage.entity.shop.OrderInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface OrderMapper {

    /**
     * 用户订单列表
     *
     * @param userId
     * @param orderStatus
     * @param pagination
     * @return
     */
    List<OrderGoodsInfo> listUserOrderGoods(@Param("userId") String userId, @Param("orderStatus") Integer orderStatus, @Param("orderType") Integer orderType, @Param("pagination") Pagination pagination);


    /**
     * 用户订单详情
     *
     * @param orderId
     * @return
     */
    OrderGoodsInfo getUserOrderGoodsInfo(@Param("userId") String userId, @Param("orderId") String orderId);

    /**
     * 后台条件查询订单列表
     *
     * @param orderNo        订单号
     * @param userName       用户名
     * @param settlementId   支付方式
     * @param logisticNumber 物流号
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @param status         状态
     * @param pagination     分页对象
     * @return
     */
    List<OrderGoodsInfo> listAdminOrderGoodsInfo(@Param("orderNo") String orderNo, @Param("userName") String userName, @Param("settlementId") Integer settlementId,
                                                 @Param("logisticNumber") String logisticNumber, @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime,
                                                 @Param("status") Integer status, @Param("pagination") Pagination pagination);

    /**
     * 扫码领取小样订单
     *
     * @param userId
     * @param orderType
     * @return
     */
    List<OrderGoodsInfo> listUserBeautyGoodsInfo(@Param("userId") String userId, @Param("orderType") Integer orderType);


    /**
     * 设置订单为已支付状态
     *
     * @param userId
     * @param orderInfoList
     * @param status
     * @return
     */
    Integer updateOrderList4Pay(@Param("userId") String userId, @Param("orderInfoList") List<OrderInfo> orderInfoList, @Param("settlementId") Integer settlementId, @Param("status") Integer status);

    /**
     * 设置订单为已支付状态
     *
     * @param userId
     * @param orderId
     * @param status
     * @return
     */
    Integer updateOrder4Pay(@Param("userId") String userId, @Param("orderId") String orderId, @Param("settlementId") Integer settlementId, @Param("orderPrice") BigDecimal orderPrice, @Param("status") Integer status);


    /**
     * 批量更新订单状态
     *
     * @param orderIds
     * @param orderStatus
     * @return
     */
    Integer updateOrderListStatus(@Param("orderIds") List<String> orderIds, @Param("orderStatus") Integer orderStatus);

    /**
     * 更新订单商品状态
     *
     * @param orderId
     * @param status
     * @return
     */
    Integer updateOrderItemsStatus(@Param("orderId") String orderId, @Param("status") Integer status);

    List<OrderInfo> listOrderByIds(@Param("orderIdList") List<String> orderIdList);

    List<OrderInfo> listUserOrderByIds(@Param("userId") String userId, @Param("orderIdList") List<String> orderIdList);

    Integer updateOrderItem4Pay(@Param("orderId") String orderId, @Param("orderPrice") BigDecimal orderPrice);
}
