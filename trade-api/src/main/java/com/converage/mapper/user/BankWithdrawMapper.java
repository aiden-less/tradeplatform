package com.converage.mapper.user;

import com.converage.architecture.dto.Pagination;
import com.converage.entity.user.BankWithdraw;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 银行卡提现
 */
@Repository
public interface BankWithdrawMapper {

    List<BankWithdraw> selectByPage(Pagination<BankWithdraw> pagination);

    int updateState(BankWithdraw bankWithdraw);
}
