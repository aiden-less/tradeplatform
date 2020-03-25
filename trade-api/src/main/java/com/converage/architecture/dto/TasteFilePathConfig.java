package com.converage.architecture.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TasteFilePathConfig {

    public static String rewardCode;
    public static String rewardCodeZip;
    public static String rsaPrivateKey;
    public static String walletEthFolder;

    @Value("${taste-file-path.reward-code}")
    public void setRewardCode(String rewardCode) {
        TasteFilePathConfig.rewardCode = rewardCode;
    }

    @Value("${taste-file-path.reward-code-zip}")
    public void setRewardCodeZip(String rewardCodeZip) {
        TasteFilePathConfig.rewardCodeZip = rewardCodeZip;
    }

    @Value("${taste-file-path.rsa-private-key}")
    public void setRsaPrivateKey(String rsaPrivateKey) {
        TasteFilePathConfig.rsaPrivateKey = rsaPrivateKey;
    }

//    @Value("${taste-file-path.wallet-wallet-folder}")
    public void setWalletEthFolder(String walletEthFolder) {
        TasteFilePathConfig.walletEthFolder = walletEthFolder;
    }
}
