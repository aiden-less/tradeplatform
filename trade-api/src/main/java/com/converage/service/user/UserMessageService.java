package com.converage.service.user;

import com.google.common.collect.ImmutableMap;
import com.converage.architecture.dto.Pagination;
import com.converage.architecture.service.BaseService;
import com.converage.client.JpushClient;
import com.converage.constance.ShopConst;
import com.converage.entity.shop.GoodsBrand;
import com.converage.entity.user.UserMessage;
import com.converage.mapper.user.UserMapper;
import com.converage.utils.ValueCheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserMessageService extends BaseService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JpushClient jpushClient;

    public void save(UserMessage userMessage) {
        ValueCheckUtils.notZero(insertIfNotNull(userMessage), "创建消息失败");
        jpushClient.pushMessage(userMessage.getTitle(), userMessage.getContent());
    }


    public List listByUserName(String userName, Pagination pagination) {
        return null;
    }

    public GoodsBrand detail(String entity) {
        return selectOneById(entity, GoodsBrand.class);
    }

    public Object operator(UserMessage userMessage, Integer operatorType) {
        Object o = null;
        switch (operatorType) {
            case ShopConst.OPERATOR_TYPE_INSERT: //添加
                o = "创建成功";
                save(userMessage);
                break;

            case ShopConst.OPERATOR_TYPE_QUERY_LIST: //查询列表
                o = listByUserName(userMessage.getUserName(), userMessage.getPagination());
                break;

        }
        return o;
    }

    public List<UserMessage> listByUserId(String userId, Integer msgType, Pagination pagination) {
        Map<String, Object> whereMap = ImmutableMap.of(
                UserMessage.User_id + "=", userId,
                UserMessage.Type + "=", msgType,
                UserMessage.If_valid + "=", true
        );

        return selectListByWhereMap(whereMap, pagination, UserMessage.class);
    }

    public UserMessage getUserMessage(String userId, String id) {
        Map<String, Object> whereMap = ImmutableMap.of(
                UserMessage.User_id + "=", userId,
                UserMessage.Id + "=", id,
                UserMessage.If_valid + "=", true
        );
        UserMessage userMessage = selectOneByWhereMap(whereMap, UserMessage.class);
        userMessage.setIfRead(true);
        update(userMessage);
        return userMessage;
    }

    public List<UserMessage> listByAll(String userId, Pagination pagination) {
        return null;
    }
}
