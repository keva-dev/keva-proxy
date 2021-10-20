package com.jinyframework.keva.proxy.command;

import com.jinyframework.keva.proxy.ServiceInstance;
import com.jinyframework.keva.proxy.util.ContextUtils;
import io.netty.channel.ChannelHandlerContext;

import java.lang.management.ManagementFactory;
import java.util.HashMap;

public class Info implements CommandHandler {
    @Override
    public void handle(ChannelHandlerContext ctx, String line) {
        final HashMap<String, Object> stats = new HashMap<>();
        final long currentConnectedClients = ServiceInstance.getConnectionService().getCurrentConnectedClients();
        final int threads = ManagementFactory.getThreadMXBean().getThreadCount();
        stats.put("clients:", currentConnectedClients);
        stats.put("threads:", threads);
        ContextUtils.write(ctx, stats.toString());
    }
}
