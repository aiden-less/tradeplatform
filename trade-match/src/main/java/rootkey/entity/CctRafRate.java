package rootkey.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by 旺旺 on 2020/3/24.
 */
@Data
public class CctRafRate implements Serializable {
    private static final long serialVersionUID = 4170326835121831594L;

    private String tradePairId; //交易对Id
    private BigDecimal fresh; //最新价
    private String tradeCoinName; //交易币种名
    private String valuationCoinName; //计价币种名
    private double rafRate;//涨跌幅

    public CctRafRate() {

    }

    public CctRafRate(String tradePairId, BigDecimal fresh, String tradeCoinName, String valuationCoinName, double rafRate) {
        this.tradePairId = tradePairId;
        this.fresh = fresh;
        this.tradeCoinName = tradeCoinName;
        this.valuationCoinName = valuationCoinName;
        this.rafRate = rafRate;
    }

}
