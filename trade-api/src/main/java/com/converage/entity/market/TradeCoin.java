package com.converage.entity.market;

import com.converage.architecture.dto.Pagination;
import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import com.converage.entity.shop.GoodsImg;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Alias("TradeCoin")
@Table(name = "trade_coin")
public class TradeCoin implements Serializable {
    private static final long serialVersionUID = -5170185157550024401L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Settlement_id)
    private Integer settlementId;

    @Column(name = Coin_name)
    private String coinName;

    @Column(name = Coin_desc)
    private String coinDesc;

    @Column(name = Main_net_id)
    private String mainNetId; //主网id

    @Column(name = If_Contract_token)
    private Boolean ifContractToken; //是否属于合约代币

    @Column(name = Contract_addr)
    private String contractAddr; //合约地址

    @Column(name = If_recharge)
    private Boolean ifRecharge;

    @Column(name = Recharge_poundage_rate)
    private BigDecimal rechargePoundageRate;

    @Column(name = Min_recharge_amount)
    private BigDecimal minRechargeAmount;

    @Column(name = Min_merge_amount)
    private BigDecimal minMergeAmount;

    @Column(name = Max_recharge_amount)
    private BigDecimal maxRechargeAmount;

    @Column(name = If_withDraw)
    private Boolean ifWithdraw;

    @Column(name = Withdraw_poundage_rate)
    private BigDecimal withDrawPoundageRate;

    @Column(name = Min_with_drawAmount)
    private BigDecimal minWithDrawAmount;

    @Column(name = Max_with_drawAmount)
    private BigDecimal maxWithDrawAmount;

    @Column(name = If_transfer)
    private Boolean ifTransfer;

    @Column(name = Transfer_poundage_rate)
    private BigDecimal transferPoundageRate;

    @Column(name = Min_transfer_amount)
    private BigDecimal minTransferAmount;

    @Column(name = Max_transfer_amount)
    private BigDecimal maxTransferAmount;

    @Column(name = Withdraw_audit_limit_amount)
    private BigDecimal withdrawAuditLimitAmount;

    @Column(name = Img_url)
    private String imgUrl;

    @Column(name = If_valid)
    private Boolean ifValid;

    @Column(name = Decimal_point)
    private Integer decimalPoint;

    private List<GoodsImg> homeImgList;
    private String userAddress;
    private Pagination pagination;

    private BigDecimal balance;

    public static final String Id = "id";
    public static final String Settlement_id = "settlement_id";
    public static final String Coin_name = "coin_name";
    public static final String Coin_desc = "coin_desc";
    public static final String Main_net_id = "main_net_id";

    public static final String If_Contract_token = "if_Contract_token";
    public static final String Contract_addr = "contract_addr";

    public static final String If_recharge = "if_recharge";
    public static final String Recharge_poundage_rate = "recharge_poundage_rate";
    public static final String Min_recharge_amount = "min_recharge_amount";
    public static final String Max_recharge_amount = "max_recharge_amount";

    public static final String If_withDraw = "if_withDraw";
    public static final String Withdraw_poundage_rate = "withdraw_poundage_rate";
    public static final String Min_with_drawAmount = "min_withdraw_amount";
    public static final String Min_merge_amount = "min_merge_amount";

    public static final String Max_with_drawAmount = "max_withdraw_amount";
    public static final String Withdraw_audit_limit_amount = "withdraw_audit_limit_amount";

    public static final String If_transfer = "if_transfer";
    public static final String Transfer_poundage_rate = "transfer_poundage_rate";
    public static final String Min_transfer_amount = "min_transfer_amount";
    public static final String Max_transfer_amount = "max_transfer_amount";

    public static final String Img_url = "img_url";
    public static final String If_valid = "if_valid";
    public static final String Decimal_point = "decimal_point";
}
