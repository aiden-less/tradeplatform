package rootkey.service;

import org.springframework.stereotype.Service;

/**
 * Created by 旺旺 on 2020/3/19.
 */
@Service
public class RedisKeyService {

    private static final String separator = "_";

    public String getKlineKey(String enumKey, String tradePairName) {
        return enumKey + separator + tradePairName;
    }


    public String getTradePairNewsKey(String enumKey, String valuationCoinName) {
        return enumKey + separator + valuationCoinName;
    }

}
