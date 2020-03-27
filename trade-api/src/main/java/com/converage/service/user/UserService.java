package com.converage.service.user;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.dto.TotalResult;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.exception.LoginException;
import com.converage.architecture.service.BaseService;
import com.converage.architecture.utils.JwtUtils;
import com.converage.entity.assets.CctAssets;
import com.converage.entity.chain.MainNetInfo;
import com.converage.entity.chain.WalletConfig;
import com.converage.entity.market.TradeCoin;
import com.converage.entity.wallet.WalletAccount;
import com.converage.service.common.GlobalConfigService;
import com.converage.service.wallet.EthService;
import com.converage.utils.*;
import com.converage.client.RedisClient;
import com.converage.constance.*;
import com.converage.entity.user.*;
import com.converage.mapper.user.UserMapper;
import com.converage.service.common.AliOSSBusiness;
import com.google.common.collect.ImmutableMap;
import com.converage.utils.eth.ETHWalletUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import static com.converage.constance.SettlementConst.*;

@Slf4j
@Service
public class UserService extends BaseService {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AliOSSBusiness aliOSSBusiness;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private UserSendService userSendService;

    @Autowired
    private UserAssetsService userAssetsService;

    @Autowired
    private GlobalConfigService globalConfigService;


    @Autowired
    private EthService ethService;


    /**
     * 注册用户
     *
     * @param paramUser
     * @throws BusinessException
     */
    public User createUser(User paramUser, Boolean validateFlag) throws BusinessException {
//        MsgRecord msgRecord = userSendService.validateMsgCode(paramUser.getUserId(), paramUser.getPhoneNumber(), paramUser.getMsgCode(), UserConst.MSG_CODE_TYPE_INVITEREGISTER);

        String phoneNumber = paramUser.getPhoneNumber();
        ValueCheckUtils.notEmpty(phoneNumber, "请输入手机号");

        String userAccount = paramUser.getUserAccount();
        ValueCheckUtils.notEmpty(userAccount, "请输入账号");

        String userAccountMatches = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
        if (!userAccount.matches(userAccountMatches)) {
            throw new BusinessException("账号须由6-16位的数字加字母组合");
        }

        String passwordMatches = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
        String password = paramUser.getPassword();
        ValueCheckUtils.notEmpty(password, "请输入密码");
        if (!password.matches(passwordMatches)) {
            throw new BusinessException("密码须由6-16位的数字加字母组合");
        }

        String msgCode = paramUser.getMsgCode();
        if (validateFlag) {
            ValueCheckUtils.notEmpty(msgCode, "请输入验证码");
        }


        User userPo = selectOneByWhereString(User.User_account + "=", userAccount, User.class);
        if (userPo != null) {
            throw new BusinessException("该账号已注册");
        }


        User registerUser = new User();
        registerUser.setPhoneNumber(phoneNumber);
        registerUser.setUserAccount(userAccount);
        registerUser.setPassword(MD5Utils.MD5Encode(password));
        String inviteCode = paramUser.getInviteCode();

        List<MainNetInfo> mainNetInfoList = selectAll(MainNetInfo.class);

        List<TradeCoin> tradeCoinList = selectAll(TradeCoin.class);
        String errorMsg = "注册失败";
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                userSendService.cancelMsgCode(userSendService.validateMsgCode(phoneNumber, msgCode, UserConst.MSG_CODE_TYPE_INVITEREGISTER));

                registerUser.setUserName(phoneNumber.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
                registerUser.setCreateTime(new Timestamp(System.currentTimeMillis()));


                registerUser.setStatus(UserConst.USER_STATUS_FROZEN);
                ValueCheckUtils.notZero(insertIfNotNull(registerUser), errorMsg);

                String registerUserId = registerUser.getId();
                paramUser.setId(registerUserId);

                List<CctAssets> userAssetList = new ArrayList<>();

                for (TradeCoin tradeCoin : tradeCoinList) {
                    String coinId = tradeCoin.getId();
                    userAssetList.add(new CctAssets(coinId, registerUserId));
                }

                ValueCheckUtils.notZero(insertBatch(userAssetList, false), errorMsg);

                String walletKey = globalConfigService.get(GlobalConfigService.Enum.WALLET_KEY);
                WalletAccount walletAccount = ETHWalletUtils.generateMnemonic(WalletConfig.ETH, registerUserId, walletKey + registerUserId);
//                String toAddress = walletAccount.getAddress();
//                String privateKey = walletAccount.getPrivateKey();
//
//                for (MainNetInfo mainNetInfo : mainNetInfoList) {
//                    MainNetUserAddr mainNetUserAddr = new MainNetUserAddr();
//                    mainNetUserAddr.setMainNetId(mainNetInfo.getId());
//                    mainNetUserAddr.setUserId(registerUserId);
//                    mainNetUserAddr.setMainNetAddr(toAddress);
//                    mainNetUserAddr.setMainNetKey(privateKey);
//                    ValueCheckUtils.notZero(insert(mainNetUserAddr), errorMsg);
//                }
            }
        });

        registerUser.setPassword("");
        registerUser.setStatus(UserConst.USER_STATUS_NORMAL);
        registerUser.setAccessToken(JwtUtils.createAppToken(registerUser));
        return registerUser;
    }

    public User loginByPhone(User user) {
        String phoneNumber = user.getPhoneNumber();
        String msgCode = user.getMsgCode();
        ValueCheckUtils.notEmpty(phoneNumber, "请输入账号");
        ValueCheckUtils.notEmpty(msgCode, "请输入验证码");

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(User.Phone_number + "=", phoneNumber);
//        paramMap.put(User.Password + "=", EncryptUtils.md5Password(password));
        User user1 = selectOneByWhereMap(paramMap, User.class);
        if (user1 == null) {
            user1 = createUser(user, true);
        } else {
            userSendService.cancelMsgCode(userSendService.validateMsgCode(phoneNumber, msgCode, UserConst.MSG_CODE_TYPE_LOGINANDREGISTER));
        }


        user1.setPassword("");
        user1.setPayPassword("");//敏感信息不返回
        user1.setAccessToken(JwtUtils.createAppToken(user1));


        if (StringUtils.isEmpty(user1.getInviteId())) {
            user1.setIfSettleInviteCode(false);
        } else {
            user1.setIfSettleInviteCode(true);
        }

        return user1;
    }


    public User loginByUserAccount(User userReq) {
        String userAccount = userReq.getUserAccount();
        String password = userReq.getPassword();
        ValueCheckUtils.notEmpty(userAccount, "请输入账号");
        ValueCheckUtils.notEmpty(password, "请输入密码");

        Map<String, Object> paramMap = new HashMap<>();

        User user1;
        String userAccountMatches = "^[0-9]{11,12}$";
        if (userAccount.matches(userAccountMatches)) {
            Map<String, Object> orderMap = ImmutableMap.of(User.Create_time, CommonConst.MYSQL_ASC);
            paramMap.put(User.Phone_number + "=", userAccount);
            List<User> userList = selectListByWhereMap(paramMap, User.class, orderMap);
            if (userList.size() > 0) {
                user1 = userList.get(0);
            } else {
                throw new BusinessException("未注册手机号");
            }
        } else {
            paramMap.put(User.User_account + "=", userAccount);
            user1 = selectOneByWhereMap(paramMap, User.class);
        }

        ValueCheckUtils.notEmpty(user1, "未注册账号");

        user1.setIfFreePayPwd(false);
        update(user1);

        if (!user1.getPassword().equals(MD5Utils.MD5Encode(password))) {
            throw new BusinessException("密码错误");
        }

        user1.setPassword("");
        user1.setPayPassword("");//敏感信息不返回
        user1.setAccessToken(JwtUtils.createAppToken(user1));


        if (StringUtils.isEmpty(user1.getInviteId())) {
            user1.setIfSettleInviteCode(false);
        } else {
            user1.setIfSettleInviteCode(true);
        }
        return user1;
    }

    /**
     * 获取用户所有信息
     *
     * @param userId
     * @return
     */
    public User allUserInfo(String userId) {
        User user = userMapper.getUserInfo(userId);
        if (user == null) {
            throw new LoginException();
        }

        Map<String, Object> orderMap = ImmutableMap.of(
                Certification.Create_time, CommonConst.MYSQL_DESC
        );
        List<Certification> certificationList = selectListByWhereString(Certification.User_id + "= ", userId, Certification.class, orderMap);
        Certification certificationPo = null;

        if (certificationList.size() > 0) {
            certificationPo = certificationList.get(0);
        }


        BigDecimal decimal1 = userAssetsService.getAssetsAmountBySettlementId(userId, SETTLEMENT_STATIC_CURRENCY);
        BigDecimal decimal2 = userAssetsService.getAssetsAmountBySettlementId(userId, SETTLEMENT_DYNAMIC_CURRENCY);

        BigDecimal computePower = decimal1.add(decimal2);


        if (StringUtils.isEmpty(user.getPayPassword())) {
            user.setIfSettlePayPwd(false);
        } else {
            user.setIfSettlePayPwd(true);
        }
        user.setPassword("");
        user.setPayPassword("");

        return user;
    }


    public User getById(String userId) {
        return userService.selectOneById(userId, User.class);
    }


    /**
     * 更新用户信息
     *
     * @param user
     */
    public void updateUserInfo(User user) {
        if (StringUtils.isNotEmpty(user.getPassword())) {
            user.setPassword(EncryptUtils.md5Password(user.getPassword()));
        }

        if (StringUtils.isNotEmpty(user.getPayPassword())) {
            user.setPayPassword(EncryptUtils.md5Password(user.getPayPassword()));
        }

        userMapper.updateUserInfo(user);
    }


    /**
     * 查询用户信息
     *
     * @param pagination
     * @return
     */
    public TotalResult<User> selectUerInfo(Pagination<User> pagination) {
        return new TotalResult<>(null, userMapper.selectUerInfo(pagination));
    }


    /**
     * 冻结
     */
    public void frozen(String userId, int status) {
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        if (updateIfNotNull(user) > 0) {
            if (status == UserConst.USER_STATUS_FROZEN) {
                redisClient.delete(String.format(RedisKeyConst.APP_ACCESS_TOKEN, userId));
            }
        }
    }


}
