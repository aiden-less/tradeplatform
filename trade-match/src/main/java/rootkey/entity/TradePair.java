package rootkey.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by 旺旺 on 2020/3/14.
 */
@Data
public class TradePair implements Serializable{

    private static final long serialVersionUID = 4168146040919282187L;

    private String id;

    private String tradeCoinId; //交易币种Id

    private String tradeCoinName; //交易币种名

    private String valuationCoinId; //计价币种Id

    private String valuationCoinName; //计价币种名

    private String pairName; //交易对名

    private BigDecimal freshPrice; //最新价

    private Boolean ifValid; //


}
