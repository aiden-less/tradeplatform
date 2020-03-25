package com.converage.entity.currency;

import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

@Data
@Alias("UserCollectCoinInfo")
@Table(name = "user_collect_coin_info")
public class UserCollectCoinInfo implements Serializable {
    private static final long serialVersionUID = 5841401723952365048L;

    @Id
    @Column(name = Id)
    private String id;

    @Column(name = User_id)
    private String userId; //用户id

    @Column(name = Coin_symbol)
    private String coinSymbol; //货币id

    //DB Column name
    public static final String Id = "id";
    public static final String User_id = "user_id";
    public static final String Coin_symbol = "coin_symbol";

    public UserCollectCoinInfo() {
    }

    public UserCollectCoinInfo(String userId, String coinSymbol) {
        this.userId = userId;
        this.coinSymbol = coinSymbol;
    }
}
