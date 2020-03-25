package com.converage.service.pay;

import com.converage.architecture.service.BaseService;
import com.converage.entity.pay.PayInfo;
import com.converage.utils.ValueCheckUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Created by 旺旺 on 2020/3/20.
 */
@Service
public class PayInfoService extends BaseService {

    public void saveBankInfo(String userId, PayInfo payInfo) {
        if (StringUtils.isNotEmpty(payInfo.getId())) {

            ValueCheckUtils.notZero(insertIfNotNull(payInfo), "更新失败");
        } else {
            ValueCheckUtils.notZero(insertIfNotNull(payInfo), "添加成功");
        }
    }

}
