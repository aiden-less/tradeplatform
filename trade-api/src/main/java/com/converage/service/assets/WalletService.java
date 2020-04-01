package com.converage.service.assets;

import com.google.common.collect.ImmutableMap;
import com.converage.architecture.dto.Pagination;
import com.converage.architecture.exception.BusinessException;
import com.converage.architecture.service.BaseService;
import com.converage.constance.CommonConst;
import com.converage.constance.ShopConst;
import com.converage.entity.assets.WalletTransferRecord;
import com.converage.entity.shop.GoodsImg;
import com.converage.entity.market.TradeCoin;
import com.converage.service.common.GlobalConfigService;
import com.converage.service.wallet.EthService;
import com.converage.utils.BigDecimalUtils;
import com.converage.utils.ValueCheckUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
public class WalletService extends BaseService {

    @Autowired
    private EthService ethService;

    @Autowired
    private GlobalConfigService globalConfigService;

    public boolean walletNameChecking(String walletName) {

        return true;
    }

    public Object operator(TradeCoin wallet, Integer operatorType) throws IOException {
        Object o = null;
        switch (operatorType) {
            case ShopConst.OPERATOR_TYPE_INSERT: //添加
                o = "创建成功";
                if (save(wallet) == 0) {
                    throw new BusinessException("创建失败");
                }
                break;

            case ShopConst.OPERATOR_TYPE_DELETE: //删除
                wallet.setIfValid(false);
                o = "删除成功";
                if (updateIfNotNull(wallet) == 0) {
                    throw new BusinessException("删除失败");
                }
                break;

            case ShopConst.OPERATOR_TYPE_QUERY_LIST: //查询列表
                o = list(wallet, wallet.getPagination());
                break;

            case ShopConst.OPERATOR_TYPE_QUERY_DETAIL: //详情
                o = detail(wallet.getId());
                break;
        }
        return o;
    }

    public Integer save(TradeCoin wallet) {

        String entityId = wallet.getId();
        if (StringUtils.isNoneBlank(entityId)) {
            ValueCheckUtils.notEmpty(selectOneById(entityId, TradeCoin.class), "未找到记录");
            return updateIfNotNull(wallet);
        } else {
            return insertIfNotNull(wallet);
        }
    }


    public List list(TradeCoin wallet, Pagination pagination) throws IOException {
        Class clazz = TradeCoin.class;
        Map<String, Object> orderMap = ImmutableMap.of(TradeCoin.Coin_name, CommonConst.MYSQL_ASC);
        Map<String, Object> whereMap = ImmutableMap.of(
                TradeCoin.If_valid + "=", true);

        //        String walletAddress = globalConfigService.get(GlobalConfigService.Enum.Merge_Transfer_Free_Address);

//        for (TradeCoin systemWallet : systemWallets) {
//            if (!systemWallet.getIfRecharge()) {
//                continue;
//            }
//
//            String contractAddress = systemWallet.getContractAddr();
//            Integer decimalPoint = systemWallet.getDecimalPoint();
//
//            BigDecimal balance = ethService.getERC20TokenBalance(walletAddress, contractAddress);
//            balance = BigDecimalUtils.divide(balance, BigDecimal.valueOf(10).pow(decimalPoint));
//            systemWallet.setBalance(balance);
//        }


        return selectListByWhereMap(whereMap, pagination, clazz, orderMap);
    }

    private TradeCoin detail(String id) {
        TradeCoin wallet = selectOneById(id, TradeCoin.class);
        GoodsImg goodsImg = new GoodsImg();
        goodsImg.setName(UUID.randomUUID().toString());
        goodsImg.setUrl(wallet.getImgUrl());
        wallet.setHomeImgList(Arrays.asList(goodsImg));

        return wallet;
    }

    public List<WalletTransferRecord> listTransferRecord(String transferType, Pagination pagination) {

        if (StringUtils.isNotEmpty(transferType)) {
            Map<String, Object> map = ImmutableMap.of(
                    CommonConst.MYSQL_DESC, WalletTransferRecord.Create_time
            );
            return selectListByWhereString(WalletTransferRecord.Transfer_type + "=", transferType, pagination, WalletTransferRecord.class, map);
        } else {
            return selectAll(pagination, WalletTransferRecord.class);
        }


    }

    public Map<String, BigDecimal> getConfigAddrBalance(Integer type) throws IOException {
        ValueCheckUtils.notEmpty(type, "请选择地址类型");

        List<TradeCoin> tradeCoins = selectAll(TradeCoin.class);


        String address;
        switch (type) {
            case 1:
                address = globalConfigService.get(GlobalConfigService.Enum.Distribute_Transfer_Free_Address);
                break;
            case 2:
                address = globalConfigService.get(GlobalConfigService.Enum.Merge_Transfer_Free_Address);
                break;
            case 3:
                address = globalConfigService.get(GlobalConfigService.Enum.Withdraw_Output_Address);
                break;
            default:
                address = "";
        }

        BigDecimal ethBalance = ethService.getETHBalance(address);

        TradeCoin usdtSW = tradeCoins.stream().filter(sw -> sw.getCoinName().equals("USDT")).findFirst().orElse(null);
        ValueCheckUtils.notEmpty(usdtSW, "USDT合约地址异常");
        Integer usdtDecimalPoint = usdtSW.getDecimalPoint();
        BigDecimal usdtBalance = ethService.getERC20TokenBalance(address, usdtSW.getContractAddr());
        usdtBalance = BigDecimalUtils.divide(usdtBalance, BigDecimal.valueOf(10).pow(usdtDecimalPoint));


        TradeCoin tcSW = tradeCoins.stream().filter(sw -> sw.getCoinName().equals("TC")).findFirst().orElse(null);
        ValueCheckUtils.notEmpty(tcSW, "合约地址异常");
        Integer tcDecimalPoint = tcSW.getDecimalPoint();
        BigDecimal tcBalance = ethService.getERC20TokenBalance(address, tcSW.getContractAddr());
        tcBalance = BigDecimalUtils.divide(tcBalance, BigDecimal.valueOf(10).pow(tcDecimalPoint));

        return ImmutableMap.of(
                "ETH", ethBalance,
                "USDT", usdtBalance,
                "TC", tcBalance
        );

    }
}
