package dev.keva.proxy.core;

import dev.keva.ioc.annotation.Autowired;
import dev.keva.ioc.annotation.Component;
import dev.keva.proxy.balance.LoadBalancingService;
import dev.keva.proxy.config.ConfigHolder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
@Component
public class NettyServer implements Runnable {

    @Autowired
    private LoadBalancingService loadBalancingService;

    @Autowired
    private ServerHandler serverHandler;

    private ConfigHolder config;
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup(1);

    public NettyServer() {
    }

    public ServerBootstrap bootstrapServer() {
        final ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
         .channel(NioServerSocketChannel.class)
         .handler(new LoggingHandler(LogLevel.INFO))
         .childHandler(new StringCodecLineFrameInitializer(serverHandler));
        return b;
    }

    public void bootstrapShards() {
        val servers = config.getServerList().split(",");
        if (servers.length == 0 || servers[0].isEmpty()) {
            throw new IllegalArgumentException("No shard server defined");
        }
        for (String server : servers) {
            loadBalancingService.addShard(server, config.getVirtualNodeAmount());
            log.info("Added shard server: " + server);
        }
    }

    public void config(ConfigHolder config) {
        this.config = config;
    }

    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    @Override
    public void run() {
        try {
            bootstrapShards();
            final ServerBootstrap server = bootstrapServer();
            server.bind(config.getPort()).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Failed to start server: ", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Failed to start server: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
