package com.converage.service.user;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.architecture.utils.JwtUtils;
import com.converage.client.RedisClient;
import com.converage.constance.CommonConst;
import com.converage.constance.RedisKeyConst;
import com.converage.entity.sys.Subscriber;
import com.converage.mapper.user.UserMapper;
import com.converage.service.common.GlobalConfigService;
import com.converage.utils.CacheUtils;
import com.converage.utils.IPUtils;
import com.converage.utils.ValueCheckUtils;
import com.converage.constance.UserConst;
import com.converage.entity.user.MsgRecord;
import com.converage.entity.user.User;
import com.converage.mapper.user.UserFreeCountMapper;
import com.converage.service.common.WjSmsService;
import com.converage.utils.RandomUtil;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.*;

@Service
public class UserSendService extends BaseService {
    @Autowired
    private UserService userService;

    @Autowired
    private WjSmsService wjSmsService;

    @Autowired
    private UserFreeCountMapper userFreeCountMapper;

    @Autowired
    private GlobalConfigService globalConfigService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 新建短信发送记录
     *
     * @throws BusinessException
     */
    public String createMsgRecord(HttpServletRequest request, User userReq) throws BusinessException, UnsupportedEncodingException {
        //验证图形验证码
        String userInputVertifyPicCode = userReq.getPicCode();
        if (StringUtils.isNotEmpty(userInputVertifyPicCode)) {
            String redisKey = RedisKeyConst.VERTIFY_PICTURE + IPUtils.getClientIP(request);
            ValueCheckUtils.notEmpty(userInputVertifyPicCode, "请输入图形验证码");
            String redisVertifyPicCode = redisClient.get(redisKey);

            if (!userInputVertifyPicCode.equalsIgnoreCase(redisVertifyPicCode)) {
                throw new BusinessException("图形验证码有误");
            }
        }

        String msgCode = RandomUtil.randomNumber(6);
        String userId;
        Integer msgType = userReq.getMsgType();
        String phoneNumber = userReq.getPhoneNumber();
        if (UserConst.MSG_CODE_TYPE_RESET_LOGINPWD == msgType) { //重置登陆密码
            List<User> users = userService.selectListByWhereString(User.Phone_number + "=", phoneNumber, null, User.class);
            ValueCheckUtils.notEmpty(users, "该手机号未注册");
            userId = phoneNumber;
        } else if (UserConst.MSG_CODE_TYPE_RESET_PAYPWD == msgType || UserConst.MSG_CODE_TYPE_SETTLE_PAYPWD == msgType) { //重置,设置支付密码
            userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
        } else {
            userId = userReq.getPhoneNumber();
        }

        User user;
        String msgTypeStr = "";
        switch (msgType) {
            case UserConst.MSG_CODE_TYPE_LOGINANDREGISTER:
                msgTypeStr = "【注册/登陆】";
                break;
            case UserConst.MSG_CODE_TYPE_INVITEREGISTER:
                msgTypeStr = "【邀请码注册】";
                break;
            case UserConst.MSG_CODE_TYPE_BINGD_PHONE:
                user = userService.selectOneByWhereString(User.Phone_number + " = ", phoneNumber, User.class);
                if (user != null) {
                    throw new BusinessException("该手机号码已经绑定其它账号");
                }
                msgTypeStr = "【绑定手机号码】";
                break;
            case UserConst.MSG_CODE_TYPE_RESET_LOGINPWD:
                msgTypeStr = "【重置登录密码】";
                break;
            case UserConst.MSG_CODE_TYPE_SETTLE_PAYPWD:
                msgTypeStr = "【设置支付密码】";
                break;
            case UserConst.MSG_CODE_TYPE_RESET_PAYPWD:
                msgTypeStr = "【重置支付密码】";
                break;
            case UserConst.MSG_CODE_TYPE_CHANGE_PHONE:
                userId = JwtUtils.getUserByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME)).getId();
//                user = userService.selectOneByWhereString(User.Phone_number + " = ", phoneNumber, User.class);
//                if (user != null) {
//                    throw new BusinessException("该手机号码已经绑定其它账号");
//                }
                msgTypeStr = "【更改手机号码】";
                break;
        }

        Map<String, String> smsSendCountMap = CacheUtils.smsSendCountMap;
        CacheUtils.putSmsCountMap(phoneNumber, msgCode);
//        String content = "【" + msgCode + "】。正在尝试" + msgTypeStr + "。验证码十五分钟内有效";
        String content = "您正在尝试" + msgTypeStr + "，验证码：" + msgCode + "，十五分钟内有效";
        wjSmsService.sendSms(userReq.getPhoneNumber(), content);
        MsgRecord msgRecord = new MsgRecord(userId, phoneNumber, msgCode, msgType, new Timestamp(System.currentTimeMillis()), true);

        ValueCheckUtils.notEmpty(insertIfNotNull(msgRecord), "发送短信验证码失败");

        return msgCode;
    }

    /**
     * 验证短信验证码
     *
     * @param phoneNumber
     * @param msgCode
     * @return
     */
    public MsgRecord validateMsgCode(String phoneNumber, String msgCode, Integer msgType) {
        ValueCheckUtils.notEmpty(phoneNumber, "手机号不能为空");
        ValueCheckUtils.notEmpty(msgCode, "短信验证码不能为空");
        ValueCheckUtils.notEmpty(msgType, "短信类型不能为空");

        Pagination pagination = new Pagination(0, 1);
        Map<String, Object> orderMap = ImmutableMap.of(MsgRecord.Create_time, CommonConst.MYSQL_DESC);
        Map<String, Object> whereMap = ImmutableMap.of(
                MsgRecord.Phone_number + " = ", phoneNumber,
                MsgRecord.Msg_type + " = ", msgType
        );
        List<MsgRecord> msgRecordList = selectListByWhereMap(whereMap, pagination, MsgRecord.class, orderMap);
        ValueCheckUtils.notZero(msgRecordList.size(), "未找到手机号所属短信验证码");
        MsgRecord msgRecord = msgRecordList.get(0);

        if(!msgCode.equals(msgRecord.getMsgCode())){
            throw new BusinessException("短信验证码有误");
        }

        if (!msgRecord.getIfValid()) {
            throw new BusinessException("短信验证码已经失效");
        }

        long minutes = DateUtils.getFragmentInMinutes(new Date(), Calendar.MINUTE);

        if (minutes > 15) {
            throw new BusinessException("短信验证码已经过期");
        }

        return msgRecord;
    }


    /**
     * 把短信验证码撤销
     *
     * @param msgRecord
     */
    public void cancelMsgCode(MsgRecord msgRecord) {
        msgRecord.setIfValid(false);
        updateIfNotNull(msgRecord);
    }

}
