package com.converage.constance;

import com.converage.utils.eth.ETHWalletUtils;
import org.apache.commons.io.FileUtils;
import org.web3j.crypto.Credentials;

import java.io.File;
import java.io.IOException;

public class WalletConst {
    public static final String ETH = "ETH";
    public static final String ETH_MAIN_NET = "https://mainnet.infura.io/v3/eac43464a30d4fcc890f3656e8290e45";
    public static final String ETH_TEST_NET = "https://rinkeby.infura.io/v3/eac43464a30d4fcc890f3656e8290e45";


    public static void main(String[] args) throws IOException {
        String walletKey = "ts-yek-tellaw";

        String keyStore = FileUtils.readFileToString(new File("F:\\keystore_71ff6c15d2ca409dac04682b85f0538b.json"), "UTF-8");
        Credentials credentials = ETHWalletUtils.loadCredentialsByKeystore(keyStore, walletKey+"71ff6c15d2ca409dac04682b85f0538b");

        String address = credentials.getAddress();
        String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
        System.out.println(address);
        System.out.println(privateKey);
    }

}
