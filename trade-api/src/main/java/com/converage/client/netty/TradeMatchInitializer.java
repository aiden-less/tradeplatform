package com.converage.client.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * Created by 旺旺 on 2020/3/18.
 */


public class TradeMatchInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline().addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
        channel.pipeline().addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));

        channel.pipeline().addLast(new TradeMatchHandler());

    }
}
