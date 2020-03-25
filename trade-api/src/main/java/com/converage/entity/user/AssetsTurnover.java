package com.converage.entity.user;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Alias("AssetsTurnover")
@Table(name = "assets_turnover")
public class AssetsTurnover implements Serializable { //资产流水
    private static final long serialVersionUID = 816842409338595448L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = User_id)
    private String userId; //用户名

    @Column(name = Turnover_title)
    private String turnoverTitle; //流水标题

    //AssetTurnoverConst.TURNOVER_TYPE_*
    @Column(name = Turnover_type)
    private Integer turnoverType; //流水类型

    @Column(name = Turnover_amount)
    private BigDecimal turnoverAmount; //流水数目

    @Column(name = After_amount)
    private BigDecimal afterAmount; //流水后数目

    @Column(name = Source_id)
    private String sourceId; //流水源Id

    @Column(name = Target_id)
    private String targetId; //流水目标Id

    @Column(name = Charge_id)
    private String chargeId; //充提转id

    //SettlementConst.SETTLEMENT_*
    @Column(name = Settlement_id)
    private Integer settlementId; //支付方式

    @Column(name = Create_time)
    private Timestamp createTime; //创建时间

    @Column(name = Detail_str)
    private String detailStr; //详情

    @Column(name = In_out_type)
    private Integer inOutType; //收入支出类型



    //扩展属性
    private String userName;
    private String userAccount;
    private String headPictureUrl;
    private String phoneNumber;
    private String settlementUnit; //支付单位
    private String settlementName; //支付中文名称
    private String typeStr; //类型描述
    private String inOutStr; //类型描述
    private String remark; //备注
    private Integer chargeStatus; //
    private String icon; //


    //DB Column name
    public static final String Id = "id";
    public static final String User_id = "user_id";
    public static final String Turnover_title = "turnover_title";
    public static final String Turnover_type = "turnover_type";
    public static final String Turnover_amount = "turnover_amount";
    public static final String After_amount = "after_amount";
    public static final String Source_id = "source_id";
    public static final String Target_id = "target_id";
    public static final String Charge_id = "charge_id";
    public static final String Settlement_id = "settlement_id";
    public static final String Create_time = "create_time";
    public static final String Detail_str = "detail_str";
    public static final String In_out_type = "in_out_type";


    public AssetsTurnover() {
    }
}
