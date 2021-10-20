package com.jinyframework.keva.proxy.util;

import io.netty.channel.ChannelHandlerContext;

public class ContextUtils {

    private ContextUtils() {
    }

    public static void write(ChannelHandlerContext ctx, String msg) {
        ctx.write(msg);
        ctx.writeAndFlush("\n");
    }
}
