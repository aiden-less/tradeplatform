package com.converage.controller.admin;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.dto.Result;
import com.converage.architecture.service.BaseService;
import com.converage.architecture.utils.JwtUtils;
import com.converage.architecture.utils.ResultUtils;
import com.converage.constance.CommonConst;
import com.converage.constance.SettlementConst;
import com.converage.constance.UserConst;
import com.converage.entity.assets.CctAssets;
import com.converage.entity.assets.UserAssetsCharge;
import com.converage.entity.sys.Subscriber;
import com.converage.entity.user.*;
import com.converage.service.assets.RechargeService;
import com.converage.service.assets.WithdrawService;
import com.converage.service.user.*;
import com.converage.utils.ExportUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 用户 会员
 */
@RequestMapping("admin/user")
@RestController
public class AdminUserController {

    @Autowired
    private BaseService baseService;

    @Autowired
    private AssetsTurnoverService assetsTurnoverService;

    @Autowired
    private CertificationService certificationService;

    @Autowired
    private BankCardService bankCardService;

    @Autowired
    private RechargeService rechargeService;

    @Autowired
    private WithdrawService withdrawService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserSendService userSendService;

    @Autowired
    private UserAssetsService userAssetsService;

    @Autowired
    private UserMessageService userMessageService;

    /**
     * 用户信息
     *
     * @return
     */
    @GetMapping("info/{userId}")
    public Result<?> info(@PathVariable String userId) {
        return ResultUtils.success(userService.allUserInfo(userId));
    }

    /**
     * 修改用户信息
     *
     * @return
     */
    @PostMapping("info/update")
    public Result<?> list(@RequestBody User user) {
        userService.updateUserInfo(user);
        return ResultUtils.success("修改用户信息成功");
    }


    /**
     * 更新会员状态
     *
     * @param id
     * @param status
     * @return
     */
    @GetMapping("status/{id}/{status}")
    public Result<?> status(@PathVariable String id, @PathVariable Integer status) {
        userService.frozen(id, status);
        return ResultUtils.success();
    }


    /**
     * 实名认证列表
     *
     * @param certification
     * @return
     */
    @RequestMapping("cert/list")
    public Result<?> certificationList(@RequestBody Certification certification) {
        Pagination pagination = certification.getPagination();
        List<Certification> certificationList = certificationService.listCertification(certification, pagination);
        return ResultUtils.success(certificationList, pagination.getTotalRecordNumber());
    }

    /**
     * 实名审核
     *
     * @param certId
     * @param status
     * @return
     */
    @RequestMapping("cert/{certId}/{status}")
    public Result<?> auth(@PathVariable String certId, @PathVariable Integer status, @RequestBody Certification certification) {
        String failReason = certification.getFailReason();
        certificationService.updateCert(certId, status, failReason);
        return ResultUtils.success("审核成功");
    }

    /**
     * 会员 流水列表
     *
     * @return
     */
    @PostMapping("assetsList")
    public Result<?> assetsList(@RequestBody Pagination<AssetsTurnover> pagination) {
        return ResultUtils.success(assetsTurnoverService.getByPage(pagination), pagination.getTotalRecordNumber());
    }

    /**
     * 会员资产详细
     *
     * @param id
     * @return
     */
    @GetMapping("assets/{id}")
    public Result<?> auth(@PathVariable String id) {
        return ResultUtils.success(baseService.selectOneByWhereString(CctAssets.User_id + " = ", id, CctAssets.class));
    }





//    /**
//     * 充提转列表
//     */
//    @PostMapping("recharge/list")
//    public Result<?> listRecharge(@RequestBody Pagination<UserAssetsCharge> pagination) {
//        return ResultUtils.success(rechargeService.listRecharge(pagination), pagination.getTotalRecordNumber());
//    }

    /**
     * 充提审核
     */
    @PostMapping("recharge/audit")
    public Result<?> auditRecharge(String id, Integer status, BigDecimal amount, HttpServletRequest request) throws IOException {
        UserAssetsCharge userAssetsCharge = baseService.selectOneById(id, UserAssetsCharge.class);
        Subscriber subscriber = JwtUtils.getAdminByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME));
        if (userAssetsCharge.getRecordType() == SettlementConst.USERASSETS_RECHARGE) {
            rechargeService.auditRecharge(id, status, subscriber);
        } else if (userAssetsCharge.getRecordType() == SettlementConst.USERASSETS_WITHDRAW) {
            withdrawService.auditWithDraw(userAssetsCharge, status, subscriber);
        }
        return ResultUtils.success("操作成功");
    }

    /**
     * 充值资产
     *
     * @param userId
     * @param rechargeAmount
     * @param rechargeSettlementId
     * @return
     */
    @PostMapping("assets/recharge")
    public Result<?> rechargeAssets(HttpServletRequest request, String userId, BigDecimal rechargeAmount, Integer rechargeSettlementId) throws UnsupportedEncodingException {
        Subscriber subscriber = JwtUtils.getAdminByToken(request.getHeader(JwtUtils.ACCESS_TOKEN_NAME));
        if (!Objects.equals(subscriber.getId(), "1")) {
            ResultUtils.error("权限不足");
        }
        String userName = subscriber.getUserName();
        rechargeService.rechargeAssets(userName, userId, rechargeAmount, rechargeSettlementId);
        return ResultUtils.success("充值成功");
    }


//    /**
//     * 导出 用户列表
//     */
//    @PostMapping("export/user")
//    public void exportUser(@RequestBody Pagination<User> pagination, HttpServletResponse response) {
//        pagination.setPageSize(ExportUtils.SHEET_MAX_MEMORY_ROW_SIZE);
//        ExportUtils.ExportDataSource<User> dataSource = new ExportUtils.ExportDataSource<User>() {
//            List<User> result = null;
//
//            public List<User> load(int pageNo, int pageSize) {
//                return userService.selectUerInfo(pagination).getList();
//            }
//
//            public String[] getColumnTitles() {
//                return new String[]{"用户名", "用户类型", "用户等级", "邀请码", "状态", "手机号码", "TC", "USDT", "TCNY", "创建时间"};
//            }
//
//            public long count() {
//                return result.size();
//            }
//
//            public Object[] convert(User user) {
//                return new Object[]{user.getUserAccount(), UserConst.getUserType(user.getType()), UserConst.getUserLevel(user.getLevel()),
//                        user.getInviteCode(), UserConst.getUserStatus(user.getStatus()), user.getPhoneNumber(), user.getCurrency(),
//                         user.getCreateTime()
//                };
//            }
//        };
//        ExportUtils.exportToExcel07(response, "用户列表.xlsx", dataSource);
//    }

//    /**
//     * 导出 充提转
//     */
//    @PostMapping("export/recharge")
//    public void exportRecharge(@RequestBody Pagination<UserAssetsCharge> pagination, HttpServletResponse response) {
//        pagination.setPageSize(ExportUtils.SHEET_MAX_MEMORY_ROW_SIZE);
//        ExportUtils.ExportDataSource<UserAssetsCharge> dataSource = new ExportUtils.ExportDataSource<UserAssetsCharge>() {
//            List<UserAssetsCharge> result = null;
//
//            public List<UserAssetsCharge> load(int pageNo, int pageSize) {
//                return rechargeService.listRecharge(pagination).getList();
//            }
//
//            public String[] getColumnTitles() {
//                return new String[]{"用户名", "资金类型", "操作类型", "数目", "状态", "截图", "创建时间", "更新时间", "备注"};
//            }
//
//            public long count() {
//                return result.size();
//            }
//
//            public Object[] convert(UserAssetsCharge userAssetsCharge) {
//                return new Object[]{userAssetsCharge.getUserAccount(), userAssetsService.getSettlementNameById(userAssetsCharge.getSettlementId()),
//                        RechargeService.getTurnoverType(userAssetsCharge.getRecordType()), userAssetsCharge.getRecordAmount(),
//                        RechargeService.getAuditByStatus(userAssetsCharge.getStatus()), userAssetsCharge.getRecordPic(),
//                        userAssetsCharge.getCreateTime(), userAssetsCharge.getUpdateTime(), userAssetsCharge.getRemark()
//                };
//            }
//        };
//        ExportUtils.exportToExcel07(response, "充提转.xlsx", dataSource);
//    }

    /**
     * 资金流水 充提转
     */
    @PostMapping("export/assetsTurnover")
    public void exportAssets(@RequestBody Pagination<AssetsTurnover> pagination, HttpServletResponse response) {
        pagination.setPageSize(ExportUtils.SHEET_MAX_MEMORY_ROW_SIZE);
        ExportUtils.ExportDataSource<AssetsTurnover> dataSource = new ExportUtils.ExportDataSource<AssetsTurnover>() {
            List<AssetsTurnover> result = null;

            public List<AssetsTurnover> load(int pageNo, int pageSize) {
                return assetsTurnoverService.getByPage(pagination).getList();
            }

            public String[] getColumnTitles() {
                return new String[]{"用户名", "手机号码", "资金类型", "交易类型", "收入支出", "数目", "操作后", "创建时间", "详情"};
            }

            public long count() {
                return result.size();
            }

            public Object[] convert(AssetsTurnover assetsTurnover) {
                return new Object[]{assetsTurnover.getUserAccount(), assetsTurnover.getPhoneNumber(),
                        userAssetsService.getSettlementNameById(assetsTurnover.getSettlementId()),
                        assetsTurnover.getTurnoverTitle(),
                        AssetsTurnoverService.getInOutType(assetsTurnover.getInOutType()), assetsTurnover.getTurnoverAmount(),
                        assetsTurnover.getAfterAmount(), assetsTurnover.getCreateTime(),
                        assetsTurnover.getDetailStr()
                };
            }
        };
        ExportUtils.exportToExcel07(response, "资产流水.xlsx", dataSource);
    }

    /**
     * 发送后台用户登录短信验证码
     */
    @GetMapping("sendAdminLoginMsg")
    public Result<?> sendAdminLoginMsg(String userName) {

        return ResultUtils.success("发送成功");
    }

    @RequestMapping("msgRecordList")
    public Result<?> msgRecordList(@RequestBody MsgRecord msgRecord) {
        Pagination pagination = msgRecord.getPagination();
        List<MsgRecord> msgRecordList;
        Map<String, Object> orderMap = ImmutableMap.of(MsgRecord.Create_time, CommonConst.MYSQL_DESC);
        if (StringUtils.isNotEmpty(msgRecord.getPhoneNumber())) {
            Map<String, Object> map = ImmutableMap.of(MsgRecord.Phone_number + "=", msgRecord.getPhoneNumber());
            msgRecordList = baseService.selectListByWhereMap(map, pagination, MsgRecord.class, orderMap);
        } else {
            msgRecordList = baseService.selectAll(pagination, MsgRecord.class, orderMap);
        }
        Integer count = pagination == null ? 0 : pagination.getTotalRecordNumber();
        return ResultUtils.success(msgRecordList, count);
    }


    @RequestMapping("message/operator/{operatorType}")
    public Result<?> operatorUserMessage(@RequestBody UserMessage userMessage, @PathVariable Integer operatorType) {
        Pagination pagination = userMessage.getPagination();
        Object object = userMessageService.operator(userMessage, operatorType);
        if (object instanceof String) {
            String message = String.valueOf(object);
            return ResultUtils.success(message);
        }

        Integer count = pagination == null ? 0 : pagination.getTotalRecordNumber();
        return ResultUtils.success(object, count);
    }
}
