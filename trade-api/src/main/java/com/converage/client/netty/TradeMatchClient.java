package com.converage.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by 旺旺 on 2020/3/18.
 */
@Component
public class TradeMatchClient {

    @Value("${trade-match.ip}")
    public String ip;

    @Value("${trade-match.port}")
    public int port;

    public void action(String msg) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        Bootstrap bs = new Bootstrap();

        bs.group(bossGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new TradeMatchInitializer() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline p = socketChannel.pipeline();
                        p.addLast("decoder", new StringDecoder());
                        p.addLast("encoder", new StringEncoder());

                        // 处理来自服务端的响应信息
                        p.addLast(new TradeMatchHandler());
                    }
                });

        // 客户端开启
        ChannelFuture cf = bs.connect(ip, port).sync();


        // 发送客户端的请求
        cf.channel().writeAndFlush(Unpooled.copiedBuffer(msg.getBytes(CharsetUtil.UTF_8)));

        // 处理完消息中断连接
        cf.channel().closeFuture();

//        // 等待直到连接中断
//        cf.channel().closeFuture().sync();
    }

}
