package com.converage.service.common;

import com.converage.architecture.dto.TasteFilePathConfig;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.entity.encrypt.EncryptEntity;
import com.converage.utils.DESUtils;
import com.converage.utils.RSAUtils;
import com.converage.utils.ValueCheckUtils;
import org.springframework.stereotype.Service;

@Service
public class RSAService extends BaseService {
    public String decryptParam(String desKeyStr, String paramEncryptStr) throws Exception {
        ValueCheckUtils.notEmpty(paramEncryptStr, "参数缺失");
        RSAUtils rsaEncryptor = new RSAUtils();
        rsaEncryptor.setPrivateKey(TasteFilePathConfig.rsaPrivateKey);

//        byte[] desKey = decryptByPrivateKey(paramEncryptStr.getBytes(), getPrivateKey(keyMap));
        String desKey = rsaEncryptor.decryptWithBase64(desKeyStr);
        return DESUtils.decryptWithBase64(paramEncryptStr, desKey);
    }

    public void checkRequestTimeout(EncryptEntity encryptEntity, Long timeLimit) {
        if (encryptEntity.getRequestTime() == null) {
            throw new BusinessException("请求时间缺失");
        }
        if ((System.currentTimeMillis() - encryptEntity.getRequestTime()) / 1000 > timeLimit) {
            throw new BusinessException("请求已超时");
        }
    }

}
