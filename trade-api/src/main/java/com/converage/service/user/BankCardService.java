package com.converage.service.user;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.constance.SettlementConst;
import com.converage.entity.pay.PayInfo;
import com.converage.mapper.user.BankWithdrawMapper;
import com.converage.utils.ValueCheckUtils;
import com.converage.constance.CommonConst;
import com.converage.constance.UserConst;
import com.converage.entity.user.BankInfo;
import com.converage.entity.user.BankWithdraw;
import com.converage.entity.user.User;
import com.converage.service.common.GlobalConfigService;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 银行卡 相关
 * Created by weihuaguo on 2018/12/23 15:44.
 */
@Service
public class BankCardService extends BaseService {

    @Autowired
    private GlobalConfigService globalConfigService;

    @Autowired
    private UserSendService userSendService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserAssetsService userAssetsService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private BankWithdrawMapper bankWithdrawMapper;

    /**
     * 用户 已经绑定 银行卡列表
     */
    public List<PayInfo> getList(String userId, Pagination pagination) {
        Map<String, Object> orderMap = ImmutableMap.of(PayInfo.Create_time, CommonConst.MYSQL_DESC);
        List<PayInfo> payInfos = selectListByWhereString(PayInfo.User_id + " = ", userId, pagination, PayInfo.class, orderMap);
        if (CollectionUtils.isEmpty(payInfos)) {
            return Collections.EMPTY_LIST;
        }
        List<BankInfo> bankInfoList = selectAll(null, BankInfo.class);
        for (PayInfo payInfo : payInfos) {
//            payInfo.setNumber(hideNumber(payInfo.getNumber()));
            for (BankInfo bankInfo : bankInfoList) {
                if (bankInfo.getName().equals(payInfo.getBankName())) {
                    break;
                }
            }
        }
        return payInfos;
    }

    /**
     * 添加银行卡
     */
    public void add(PayInfo payInfo, String msgCode, String bankId) {
        String userId = payInfo.getUserId();
//        if (StringUtils.isBlank(payInfo.getNumber()) || StringUtils.isBlank(payInfo.getIdCard()) || StringUtils.isBlank(bankId)
//                || StringUtils.isBlank(msgCode)) {
//            throw new BusinessException("缺少必要参数");
//        }
        BankInfo bankInfo = selectOneById(bankId, BankInfo.class);
        if (bankInfo == null) {
            throw new BusinessException("暂时不能添加这个银行");
        }
//        Map<String, Object> whereMap = ImmutableMap.of(PayInfo.User_id + " = ", userId, PayInfo.Number + " = ", payInfo.getNumber());
//        if (selectOneByWhereMap(whereMap, PayInfo.class) != null) {
//            throw new BusinessException("您已经添加了该银行卡号");
//        }
        userSendService.validateMsgCode(selectOneById(userId, User.class).getPhoneNumber(), msgCode, UserConst.MSG_CODE_TYPE_BIND_BANK);
        payInfo.setBankName(bankInfo.getName());
        insertIfNotNull(payInfo);
    }

    /**
     * 删除银行卡
     */
    public void remove(PayInfo payInfo) {
        PayInfo selectBank = selectOneById(payInfo.getId(), PayInfo.class);
        if (selectBank == null || !selectBank.getUserId().equals(payInfo.getUserId())) {
            throw new BusinessException("银行卡数据异常");
        }
        ValueCheckUtils.notZero(delete(payInfo), "删除失败");
    }

    /**
     * 用户 银行卡 提现列表
     */
    public List<BankWithdraw> withdrawList(String userId, Pagination pagination) {
        Map<String, Object> orderMap = ImmutableMap.of(PayInfo.Create_time, CommonConst.MYSQL_DESC);
        List<BankWithdraw> bankWithdrawList = selectListByWhereString(BankWithdraw.User_id + " = ",
                userId, pagination, BankWithdraw.class, orderMap);
        if (CollectionUtils.isEmpty(bankWithdrawList)) {
            return Collections.EMPTY_LIST;
        }
        for (BankWithdraw bankWithdraw : bankWithdrawList) {
            bankWithdraw.setNumber(hideNumber(bankWithdraw.getNumber()));
        }
        return bankWithdrawList;
    }

    /**
     * 后台 银行卡 提现列表
     */
    public List<BankWithdraw> listBankWithdraw(Pagination<BankWithdraw> pagination) {
        return bankWithdrawMapper.selectByPage(pagination);
    }

    /**
     * 更新 银行卡提现 状态
     */
    public void updateBankWithdraw(String id, int state) {
        BankWithdraw bankWithdraw = selectOneById(id, BankWithdraw.class);
        if (bankWithdraw == null || bankWithdraw.getState() != UserConst.WITHDRAW_STATE_PENDING) {
            throw new BusinessException("数据状态异常");
        }
        int result1;
        int result2;
        if (state == UserConst.WITHDRAW_STATE_PASSED) {
            //通过, 仅更新状态
            result2 = 1;
        } else if (state == UserConst.WITHDRAW_STATE_NOTPASS) {
            //不通过, 退款
            result2 = userAssetsService.increaseUserAssets(bankWithdraw.getUserId(), bankWithdraw.getAmount(), SettlementConst.SETTLEMENT_RMB);
        } else {
            throw new BusinessException("参数异常");
        }
        bankWithdraw.setState(state);
        result1 = bankWithdrawMapper.updateState(bankWithdraw);
        if (result1 < 1 || result2 < 1) {
            throw new BusinessException("操作失败");
        }
    }

    /**
     * 隐藏卡号
     *
     * @param number
     * @return
     */
    private String hideNumber(String number) {
        int beginIndex = number.length() - 3;
        return number.substring(0, 4) + " **** **** " + number.substring(beginIndex, (beginIndex + 3));
    }

}
