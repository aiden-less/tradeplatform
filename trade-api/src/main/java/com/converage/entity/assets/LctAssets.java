package com.converage.entity.assets;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Alias("LctAssets")
@Table(name = "lct_assets")
public class LctAssets implements Serializable {
    private static final long serialVersionUID = 3358066365525820657L;


    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Coin_id)
    private String coinId; //币种id

    @Column(name = User_id)
    private String userId; //用户id

    @Column(name = Assets_amount)
    private BigDecimal assetsAmount; //可用资产数目


    private String coinName;
    private BigDecimal freeAmount;//可用数目
    private BigDecimal frozenAmount;//冻结数目
    private List<CctFinanceLog> financeLogs;//财务记录

    //DB Column name
    public static final String Id = "id";
    public static final String Coin_id = "coin_id";
    public static final String User_id = "user_id";
    public static final String Assets_amount = "assets_amount";

    public LctAssets() {

    }

    public LctAssets(String coinId, String userId) {
        this.coinId = coinId;
        this.userId = userId;
    }
}
