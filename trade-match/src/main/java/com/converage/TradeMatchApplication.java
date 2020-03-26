package com.converage;

import com.converage.middleware.netty.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetSocketAddress;

@SpringBootApplication
@EnableScheduling
public class TradeMatchApplication implements CommandLineRunner {

    @Autowired
    private NettyServer nettyServer;

    private static final Logger logger = LoggerFactory.getLogger(TradeMatchApplication.class);

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(TradeMatchApplication.class, args);
    }

    @Override
    public void run(String... strings) {
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 9999);
        logger.info("交易撮合服务器启动" + "IP：127.0.0.1，port：9999");
        nettyServer.start(address);

    }

}
