package com.converage.service.user;


import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.entity.user.*;
import com.converage.utils.CacheUtils;
import com.converage.utils.ValueCheckUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 用户升级
 */
@Service
public class UserUpgradeService extends BaseService {
    public Boolean validateIfUpgrade(User user, UserNode userNode) {
        if (userNode == null) {
            return false;
        }

        Boolean flag = false;
        UserSocial us = CacheUtils.getSocialMap(user.getId());
        BigDecimal socialAchievement = us.getSocialAchievement();
        BigDecimal targetAchievement = userNode.getSocialAchievement();
        if (socialAchievement.compareTo(targetAchievement) >= 0) {
            flag = true;
        }

        return flag;
    }


    public void upgrade(String userId) {
        User user = selectOneById(userId, User.class);
        ValueCheckUtils.notEmpty(user, "未找到用户");

        UserNode userNodes = CacheUtils.getUserNodeMap(user.getLevel() + 1);
        ValueCheckUtils.notEmpty(userNodes, "等级已达到上限");

        Boolean flag = validateIfUpgrade(user, userNodes);
        if (flag) {
            user.setLevel(user.getLevel() + 1);
            update(user);
        } else {
            throw new BusinessException("未达到升级条件");
        }
    }
}
