package com.converage;

import com.converage.service.wallet.BtcService;
import com.converage.utils.HttpClientUtils;
import com.google.common.collect.ImmutableMap;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet2Params;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

/**
 * Created by 旺旺 on 2020/3/27.
 */
@SpringBootTest
public class BtcTest {

    @Autowired
    private BtcService btcService;

    @Test
    public void test() {
//        NetworkParameters params = MainNetParams.get();//生成正式链地址用这个
        NetworkParameters params = TestNet2Params.get();//test2
        //NetworkParameters params = TestNet3Params.get();//test3

        //生成地址
        ECKey key = new ECKey();
        System.out.println("地址：" + key.toAddress(params).toString());
        System.out.println("公钥：" + key.getPublicKeyAsHex());
        System.out.println("私钥（但是这个私钥导入不了IMtoken）：" + key.getPrivateKeyAsHex());
        System.out.println("私钥（可以导进IMtoken）：" + key.getPrivateKeyAsWiF(params));

    }


    @Test
    public void test2() {
        String url = "https://api.omniwallet.org/v2/address/addr";
        Map<String, String> map = ImmutableMap.of(
                "addr", "1FoWyxwPXuj4C6abqwhjDWdz6D4PZgYRjA"
        );
        String str = HttpClientUtils.doPost(url, map);
        System.out.println(str);
    }


}
