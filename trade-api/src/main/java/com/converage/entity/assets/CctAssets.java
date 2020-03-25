package com.converage.entity.assets;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.math.BigDecimal;

//币币账号资产
@Data
@Alias("CctAssets")
@Table(name = "cct_assets")
public class CctAssets implements Serializable {
    private static final long serialVersionUID = 5279191356481637406L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = Coin_id)
    private String coinId; //币种id

    @Column(name = User_id)
    private String userId; //用户id

    @Column(name = Assets_amount)
    private BigDecimal assetsAmount; //资产数目


    //DB Column name
    public static final String Id = "id";
    public static final String Coin_id = "coin_id";
    public static final String User_id = "user_id";
    public static final String Assets_amount = "assets_amount";

    public CctAssets() {

    }

    public CctAssets(String coinId, String userId) {
        this.coinId = coinId;
        this.userId = userId;
    }
}
