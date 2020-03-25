package com.converage.controller.admin;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.dto.Result;
import com.converage.architecture.utils.ResultUtils;
import com.converage.entity.shop.OrderGoodsInfo;
import com.converage.entity.shop.OrderInfo;
import com.converage.service.shop.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("admin/order")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;


    /**
     * 条件查询订单列表
     *
     * @param orderInfo 条件查询实体
     * @return
     */
    @RequestMapping("list")
    public Result<?> list(@RequestBody OrderInfo orderInfo) {
        Pagination pagination = orderInfo.getPagination();
        List<OrderGoodsInfo> orderInfoList = orderService.listAdminOrderGoodsInfo(orderInfo, pagination);
        return ResultUtils.success(orderInfoList, pagination);
    }

    /**
     * 更新订单物流号
     *
     * @param orderInfo 条件查询实体
     * @return
     */
    @RequestMapping("deliver")
    public Result<?> deliver(@RequestBody OrderInfo orderInfo) {
        orderService.updateOrderLogisticNumber(orderInfo.getId(), orderInfo.getLogisticNumber());
        return ResultUtils.success("更新订单物流号成功");
    }


}
