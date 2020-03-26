package com.converage.entity.wallet;

import lombok.Data;

import java.io.Serializable;

@Data
public class WalletAccount implements Serializable{
    private static final long serialVersionUID = 2940326680778351049L;

    private Long id;
    public String address;
    private String name;
    private String password;
    private String keystorePath;
    private String mnemonic;
    private String publicKey;
    private String privateKey;
}
