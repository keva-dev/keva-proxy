package dev.keva.proxy.command;

import dev.keva.ioc.annotation.Autowired;
import dev.keva.ioc.annotation.Component;
import dev.keva.proxy.util.ConnectionService;
import dev.keva.proxy.util.ContextUtils;
import io.netty.channel.ChannelHandlerContext;

import java.lang.management.ManagementFactory;
import java.util.HashMap;

@Component
public class Info implements CommandHandler {

    @Autowired
    private ConnectionService connectionService;

    @Override
    public void handle(ChannelHandlerContext ctx, String line) {
        final HashMap<String, Object> stats = new HashMap<>();
        final long currentConnectedClients = connectionService.getCurrentConnectedClients();
        final int threads = ManagementFactory.getThreadMXBean().getThreadCount();
        stats.put("clients:", currentConnectedClients);
        stats.put("threads:", threads);
        ContextUtils.write(ctx, stats.toString());
    }
}
