package com.converage.service.sys;

import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.client.RedisClient;
import com.converage.constance.RedisKeyConst;
import com.converage.entity.sys.Subscriber;
import com.converage.entity.sys.SubscriberRole;
import com.converage.mapper.sys.SubscriberMapper;
import com.converage.utils.MD5Utils;
import com.converage.utils.ValueCheckUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubscriberService extends BaseService {

    @Autowired
    private SubscriberMapper subscriberMapper;

    @Autowired
    private RedisClient redisClient;

    /**
     * 删除用户角色列表
     */
    private Integer deleteRoles(String subscriberId) {
        return subscriberMapper.deleteRoles(subscriberId);
    }

    /**
     * 创建用户
     */
    public void insertSubscriber(Subscriber subscriber) {
        Subscriber subscriberPo = selectOneByWhereString(Subscriber.User_name + "=", subscriber.getUserName(), Subscriber.class);
        if (subscriberPo != null) {
            throw new BusinessException("账号已存在");
        }
        setPassword(subscriber);
        insertIfNotNull(subscriber);
        setRoles(subscriber);
    }

    /**
     * 更新用户 信息
     */
    @Transactional
    public void updateSubscriber(Subscriber subscriber) {
        String subscriberId = subscriber.getId();
        Subscriber subscriberPo = selectOneById(subscriberId, Subscriber.class);
        if (subscriberPo == null) {
            throw new BusinessException("未找到用户");
        }
        setPassword(subscriber);
        updateIfNotNull(subscriber);
        deleteRoles(subscriberId);
        setRoles(subscriber);
    }

    /**
     * 用户名密码 校验
     */
    public Subscriber getSubscriberByLogin(String userName, String password) {
        if (StringUtils.isBlank(userName)) {
            throw new BusinessException("请输入用户名");
        }
        if (StringUtils.isBlank(password)) {
            throw new BusinessException("请输入密码");
        }
        Subscriber subscriber = subscriberMapper.selectSubscriberByLogin(userName);
        ValueCheckUtils.notEmpty(subscriber, "用户名或者密码错误");
        if (subscriber.getPassword().equals(MD5Utils.MD5Encode(password + subscriber.getSalt()))) {
            return subscriber;
        } else {
            throw new BusinessException("用户名或者密码错误");
        }
    }

    public Subscriber listRole(String subscriberId) {
        return subscriberMapper.listRole(subscriberId);
    }

    /**
     * 设置密码
     */
    private void setPassword(Subscriber subscriber) {
        String password = subscriber.getPassword();
        if (StringUtils.isNotBlank(password)) {
            String salt = RandomStringUtils.randomAlphanumeric(6);
            subscriber.setPassword(MD5Utils.MD5Encode(password + salt));
            subscriber.setSalt(salt);
            logout(subscriber.getId());
        }
    }

    /**
     * 设置用户角色列表
     */
    private void setRoles(Subscriber subscriber) {
        List<String> roleIdList = subscriber.getRoleIdList();
        if (CollectionUtils.isNotEmpty(roleIdList)) {
            List<SubscriberRole> subscriberRoles = new ArrayList<>();
            for (String roleId : roleIdList) {
                subscriberRoles.add(new SubscriberRole(subscriber.getId(), roleId));
            }
            insertBatch(subscriberRoles, false);
        }
    }

    /**
     * 修改密码
     */
    public void changePassword(String id, String password, String newPassword) {
        ValueCheckUtils.notEmpty(password, "请输入原密码");
        ValueCheckUtils.notEmpty(newPassword, "请输入新密码");
        Subscriber subscriber = selectOneById(id, Subscriber.class);
        ValueCheckUtils.notEmpty(subscriber, "数据不存在");
        if (!subscriber.getPassword().equals(MD5Utils.MD5Encode(password + subscriber.getSalt()))) {
            throw new BusinessException("原密码错误");
        }
        Subscriber subscriber1 = new Subscriber();
        subscriber1.setId(id);
        subscriber1.setPassword(newPassword);
        setPassword(subscriber1);
        updateIfNotNull(subscriber1);
        logout(id);
    }

    /**
     * 退出登录
     */
    public void logout(String adminId) {
        redisClient.delete(String.format(RedisKeyConst.ADMIN_ACCESS_TOKEN, adminId));
    }
}
