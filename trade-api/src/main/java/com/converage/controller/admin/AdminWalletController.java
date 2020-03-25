package com.converage.controller.admin;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.dto.Result;
import com.converage.architecture.utils.ResultUtils;
import com.converage.entity.assets.WalletTransferRecord;
import com.converage.entity.market.TradeCoin;
import com.converage.service.assets.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RequestMapping("admin/wallet")
@RestController
public class AdminWalletController {


    @Autowired
    private WalletService walletService;

    @RequestMapping("operator/{operatorType}")
    public Result<?> operatorMiningMachine(HttpServletRequest request, @RequestBody TradeCoin wallet, @PathVariable Integer operatorType) throws IOException {
        Pagination pagination = wallet.getPagination();
        Object object = walletService.operator(wallet, operatorType);

        if (object instanceof String) {
            String message = String.valueOf(object);
            return ResultUtils.success(message);
        }
        Integer count = pagination == null ? 0 : pagination.getTotalRecordNumber();
        return ResultUtils.success(object, count);
    }

    @RequestMapping("transferRecord/list")
    public Result<?> walletTransferList(@RequestBody WalletTransferRecord wtr) {
        String transferType = wtr.getTransferType();
        Pagination pagination = wtr.getPagination();

        List<WalletTransferRecord> wtrList = walletService.listTransferRecord(transferType, pagination);

        Integer count = pagination == null ? 0 : pagination.getTotalRecordNumber();
        return ResultUtils.success(wtrList, count);
    }


    @RequestMapping("balance/{type}")
    public Result<?> walletTransferList(@PathVariable Integer type) throws IOException {
        return ResultUtils.success(walletService.getConfigAddrBalance(type));
    }
}
