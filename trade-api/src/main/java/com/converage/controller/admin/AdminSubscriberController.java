package com.converage.controller.admin;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.dto.Result;
import com.converage.entity.sys.Subscriber;
import com.converage.service.sys.FunctionService;
import com.converage.service.sys.SubscriberService;
import com.converage.architecture.utils.JwtUtils;
import com.converage.architecture.utils.ResultUtils;
import com.google.common.collect.ImmutableMap;
import com.converage.service.user.UserAssetsService;
import com.converage.service.user.UserSendService;
import com.converage.utils.ValueCheckUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("admin/subscriber")
public class AdminSubscriberController {

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private FunctionService functionService;

    @Autowired
    private UserAssetsService userAssetsService;

    @Autowired
    private UserSendService userSendService;

    @RequestMapping("create")
    public Result<?> create(@RequestBody Subscriber subscriber) {
        subscriberService.insertSubscriber(subscriber);
        return ResultUtils.success("创建用户成功");
    }

    @RequestMapping("list")
    public Result<?> list(@RequestBody Subscriber subscriber) {
        Pagination pagination = subscriber.getPagination();
        Map<String, Object> whereMap = new HashMap<>();
        if (StringUtils.isNoneBlank(subscriber.getUserName())) {
            whereMap = ImmutableMap.of(Subscriber.User_name + " like ", "%" + subscriber.getUserName() + "%");
        }
        List<Subscriber> subscriberList = subscriberService.selectListByWhereMap(whereMap, pagination, Subscriber.class);

        Integer count = pagination == null ? subscriberList.size() : pagination.getTotalRecordNumber();
        return ResultUtils.success(subscriberList, count);
    }

    @RequestMapping("id")
    public Result<?> list(HttpServletRequest request) {
        String adminId = JwtUtils.getAdminIdByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME));
        return ResultUtils.success(ImmutableMap.of("subscriberId",adminId));
    }


    @RequestMapping("login")
    public Result<?> login(HttpServletRequest request, @RequestBody Subscriber subscriber) {
        String subscriberId = null;
        String loginToken = subscriber.getLoginToken();
        if (StringUtils.isNotBlank(loginToken)) {
            if (!JwtUtils.verifyJwt4Admin(loginToken)) {
                return ResultUtils.error("请重新登录");
            }
            subscriberId = JwtUtils.getAdminIdByToken(loginToken);
        }
        Subscriber subscriberPo;
        if (StringUtils.isNoneBlank(subscriberId)) {
            subscriberPo = subscriberService.selectOneById(subscriberId, Subscriber.class);
        } else {
            String userName = subscriber.getUserName();
            String password = subscriber.getPassword();
            subscriberPo = subscriberService.getSubscriberByLogin(userName, password);

        }
        ValueCheckUtils.notEmpty(subscriberPo, "用户名错误");

        subscriberPo.setFuncTreeNode(functionService.queryFuncTree(subscriberPo.getId()));
        subscriberPo.setLoginToken(JwtUtils.createAdminToken(subscriberPo));
        return ResultUtils.success(subscriberPo);
    }

    @RequestMapping("edit")
    public Result<?> edit(@RequestBody Subscriber subscriberReq) {
        Subscriber subscriber = subscriberService.listRole(subscriberReq.getId());
        return ResultUtils.success(subscriber);
    }

    @RequestMapping("update")
    public Result<?> update(@RequestBody Subscriber subscriberReq) {
        subscriberService.updateSubscriber(subscriberReq);
        return ResultUtils.success("保存成功");
    }


    @RequestMapping("turnoverCountAmount")
    public Result<?> turnoverCountAmount() {
        return ResultUtils.success(userAssetsService.countTurnoverAmount());
    }

    /**
     * 修改密码
     */
    @PostMapping("changePassword")
    public Result<?> changePassword(HttpServletRequest request, String password, String newPassword) {
        String adminId = JwtUtils.getAdminIdByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME));
        subscriberService.changePassword(adminId, password, newPassword);
        return ResultUtils.success();
    }

    /**
     * 修改密码
     */
    @PostMapping("logout")
    public Result<?> logout(HttpServletRequest request) {
        String adminId = JwtUtils.getAdminIdByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME));
        subscriberService.logout(adminId);
        return ResultUtils.success();
    }
}
