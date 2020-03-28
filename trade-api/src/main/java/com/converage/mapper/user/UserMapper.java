package com.converage.mapper.user;

import com.converage.architecture.dto.Pagination;
import com.converage.entity.sys.UserTreeNode;
import com.converage.entity.user.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface UserMapper {

    /**
     * 获取所有用户（用于展示邀请记录）
     *
     * @return
     */
    List<User> listUser4InviteRecord(@Param("memberType") Integer memberType);

    /**
     * 根据更新字段重置密码
     *
     * @param userId          用户id
     * @param newPassword     新密码
     * @param updatePWDColumn 更新字段
     */
    Integer updatePassword(@Param("userId") String userId, @Param("newPassword") String newPassword, @Param("updatePWDColumn") String updatePWDColumn);

    /**
     * 根据更新字段查询用户
     *
     * @param userId
     * @param updatePWDColumn
     * @param oldPassword
     * @return
     */
    User getUserToUpdatePwd(@Param("userId") String userId, @Param("updatePWDColumn") String updatePWDColumn, @Param("oldPassword") String oldPassword);

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    User getUserInfo(@Param("userId") String userId);



    /**
     * 删除微信信息
     *
     * @param userId
     * @return
     */
    int deleteWeixin(String userId);

    /**
     * 用户直接邀请数量
     */
    Integer selectInviteCountByUserId(String userId);

    /**
     * 后台更新用户信息
     *
     * @param user
     */
    void updateUserInfo(User user);


    /**
     * 查询用户信息
     *
     * @param pagination
     * @return
     */
    List<User> selectUerInfo(Pagination<User> pagination);

    User selectTotal(Pagination<User> pagination);

    /**
     * 查找第一个注册用户
     *
     * @return
     */
    User selectFirstUser();

    /**
     * 查询未购买矿机的用户
     *
     * @return
     */
    List<User> listUserNoneMiningMachine();

    BigDecimal countRewardValue(@Param("userId") String userId);

    BigDecimal countRewardScore(@Param("userId") String userId);

    List<UserMessage> listUserMessage(@Param("userAccount") String userName, @Param("pagination") Pagination pagination);

    List<UserMessage> listAllMessage(@Param("userId") String userId, @Param("pagination") Pagination pagination);

    Integer checkSendSMSSecondRate(@Param("phoneNumber") String phoneNumber, @Param("msgType") Integer msgType, @Param("secondNum") Integer secondNum);

    Integer countDirectInviteUser(@Param("userId") String userId);

    Integer updateInviteTeamId(@Param("leaderUserId") String inviteUserId, @Param("registerUserId") String registerUserId);

    List<UserTreeNode> selectUserTree();

    Integer increaseComputeReward(@Param("userId") String userId, @Param("reward") BigDecimal reward);

    List<User> listOldUser();

    Integer updateAddress(@Param("userId") String id, @Param("toAddress") String toAddress);
}
