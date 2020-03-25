package com.converage.controller.app;

import com.converage.architecture.dto.Result;
import com.converage.architecture.utils.ResultUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 旺旺 on 2020/3/24. 行情controller
 */
@RequestMapping(value = "/app/quotation")
@RestController
public class AppQuotationController {


    //个人收藏行情
    public Result<?> listCollectQuotation() {


        return ResultUtils.success();
    }

    //所有行情
    public Result<?> listQuotation(String valuationCoinId) {


        return ResultUtils.success();
    }


}
