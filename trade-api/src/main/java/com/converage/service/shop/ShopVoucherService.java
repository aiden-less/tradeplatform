package com.converage.service.shop;

import com.converage.architecture.service.BaseService;
import com.converage.constance.ShopConst;
import org.springframework.stereotype.Service;

@Service
public class ShopVoucherService extends BaseService{

    /**
     * 判断是否属于礼包卡券
     * @param voucherType
     * @return
     */
    public Boolean checkIfPackageVoucher(Integer voucherType){
        if(ShopConst.VOUCHER_TYPE_BEAUTY_PACKAGE_DEDUCTION ==voucherType||ShopConst.VOUCHER_TYPE_BIRTH_PACKAGE_DEDUCTION ==voucherType){
            return true;
        }
        return false;
    }



}
