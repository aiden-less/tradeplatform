package com.converage.service.user;

import com.converage.service.assets.UserAssetsService;
import com.google.common.collect.ImmutableMap;
import com.converage.architecture.dto.Pagination;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.client.RedisClient;
import com.converage.constance.*;
import com.converage.mapper.user.UserMapper;
import com.converage.service.common.GlobalConfigService;
import com.converage.utils.AliyunInterface;
import com.converage.utils.CacheUtils;
import com.converage.utils.ValueCheckUtils;
import com.converage.entity.user.Certification;
import com.converage.entity.user.User;
import com.converage.mapper.user.CertificationMapper;
import com.converage.service.common.AliOSSBusiness;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CertificationService extends BaseService {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private AliOSSBusiness aliOSSBusiness;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CertificationMapper certificationMapper;

    @Autowired
    private UserAssetsService userAssetsService;

    @Autowired
    private AssetsTurnoverService assetsTurnoverService;

    @Autowired
    private GlobalConfigService globalConfigService;

    @Autowired
    private RedisClient redisClient;

    /**
     * app端申请实名认证
     *
     * @param userId        用户id
     * @param realName
     * @param licenseNumber
     * @param frontFiles    图片
     * @param backFiles     图片
     * @param handleFiles
     */
    public void createCert(String userId, String realName, String licenseNumber,
                           MultipartFile frontFiles, MultipartFile backFiles, MultipartFile handleFiles) {

        CacheUtils.putCertCountMap(licenseNumber);

        Integer i = certificationMapper.countCertedOrIng(licenseNumber);
        if (i > 0) {
            throw new BusinessException("该证件号已被认证或认证中");
        }

        Map<String, Object> orderMap = ImmutableMap.of(
                Certification.Create_time, CommonConst.MYSQL_DESC
        );
        List<Certification> certificationList = selectListByWhereString(Certification.User_id + "= ", userId, Certification.class, orderMap);
        Certification certificationPo = null;
        if (certificationList.size() > 0) {
            certificationPo = certificationList.get(0);
            if (UserConst.USER_CERT_STATUS_ING == certificationPo.getStatus()) {
                throw new BusinessException("该用户处于认证审核状态,请勿重复提交");
            }
            if (UserConst.USER_CERT_STATUS_PASS == certificationPo.getStatus()) {
                throw new BusinessException("该用户已通过认证.请勿重复提交");
            }
        }
        ValueCheckUtils.notEmpty(frontFiles, "证件正面图不能为空");
        ValueCheckUtils.notEmpty(frontFiles, "证件背面图不能为空");
        ValueCheckUtils.notEmpty(handleFiles, "证件手持图不能为空");

        String result = AliyunInterface.idCardVertify(realName, licenseNumber, "GET");
        JSONObject object = JSONObject.fromObject(result);
        if (!object.getString("status").equals("01")) {
            throw new BusinessException(object.getString("msg"));
        }

        Certification certification = new Certification();
        certification.setPositivePhotoUrl(aliOSSBusiness.uploadCommonPic(frontFiles));
        certification.setReversePhotoUrl(aliOSSBusiness.uploadCommonPic(backFiles));
        certification.setHandlePhotoUrl(aliOSSBusiness.uploadCommonPic(handleFiles));
        certification.setUserId(userId);
        certification.setRealName(realName);
        certification.setLicenseNumber(licenseNumber);
        certification.setStatus(UserConst.USER_CERT_STATUS_ING);

        String errorMsg = "申请实名认证失败,请稍后再试";
        try {
            if (certificationPo == null) {
                ValueCheckUtils.notZero(insertIfNotNull(certification), errorMsg);
            } else {
                certification.setId(certificationPo.getId());
                ValueCheckUtils.notZero(updateIfNotNull(certification), errorMsg);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException("申请实名认证失败,请稍后再试");
        }

    }


    /**
     * 条件查询实名认证申请列表
     *
     * @return
     */
    public List<Certification> listCertification(Certification certification, Pagination pagination) {
        return certificationMapper.listCertification(certification.getUserAccount(), certification.getRealName(), certification.getLicenseNumber(), certification.getStatus(), pagination);
    }

    public Certification getCertificationByUserId(String userId) {
        return selectOneByWhereString(Certification.User_id + "=", userId, Certification.class);
    }


    /**
     * 审核实名认证
     *
     * @param certId        实名认证记录id
     * @param vertifyStatus 审核状态
     */
    public void updateCert(String certId, Integer vertifyStatus, String failReason) {
        Certification certificationPo = selectOneById(certId, Certification.class);
        ValueCheckUtils.notEmpty(certificationPo, "未找到申请实名记录");

        Map<String, Object> whereMap1 = ImmutableMap.of(
                Certification.License_number + "= ", certificationPo.getLicenseNumber(),
                Certification.Status + "=", UserConst.USER_CERT_STATUS_PASS
        );
        Certification certificationValidate = selectOneByWhereMap(whereMap1, Certification.class);
        if (certificationValidate != null && vertifyStatus == UserConst.USER_CERT_STATUS_PASS) {
            throw new BusinessException("该证件号已被认证，请执行驳回操作");
        }

        if (!(certificationPo.getStatus() == UserConst.USER_CERT_STATUS_ING)) {
            throw new BusinessException("申请记录已审核");
        }
        String userId = certificationPo.getUserId();
        User user = selectOneById(userId, User.class);


        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                Certification certification = new Certification();
                certification.setId(certId);
                certification.setFailReason(failReason);
                certification.setStatus(vertifyStatus);

                String errorMsg = "审核失败，请稍后再试";
                try {
                    ValueCheckUtils.notZero(updateIfNotNull(certification), errorMsg);
                } catch (Exception e) {
                    throw new BusinessException(errorMsg);
                }


                Map<String, Object> map = ImmutableMap.of(
                        User.Id, user.getId(),
                        User.Invite_id, user.getInviteId()
                );
                redisClient.put(RedisKeyConst.ALL_USER_SAMPLE_INFO, user.getId(), map);
            }

        });

    }

}
