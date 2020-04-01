package com.converage.entity.chain;

import com.alibaba.fastjson.JSONObject;
import com.converage.architecture.mybatis.annotation.Column;
import com.converage.architecture.mybatis.annotation.Id;
import com.converage.architecture.mybatis.annotation.Table;
import lombok.Data;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by 旺旺 on 2020/3/26.
 */
@Data
@Table(name = "wallet_config")
public class WalletConfig implements Serializable {

    public static String PrivKeyRule = "O^QYl@Sjx@L3UYBT";

    public static String BTC = "BTC";
    public static String ETH = "ETH";
    public static String USDT = "USDT";



    private static final long serialVersionUID = -8045584506645472898L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "user")
    private String user;

    @Column(name = "password")
    private String password;

    @Column(name = "host")
    private String host;

    @Column(name = "port")
    private String port;

    @Column(name = "timeout")
    private int timeout;

}
