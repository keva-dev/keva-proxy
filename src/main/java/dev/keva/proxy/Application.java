package dev.keva.proxy;

import dev.keva.ioc.KevaIoC;
import dev.keva.proxy.config.ConfigHolder;
import dev.keva.proxy.config.ConfigManager;
import dev.keva.proxy.core.NettyServer;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public final class Application {
    public static void main(String[] args) {
        try {
            KevaIoC ioc = KevaIoC.initBeans(Application.class);
            final ConfigHolder configHolder = ConfigManager.loadConfig(args);
            log.info(configHolder.toString());
            val server = ioc.getBean(NettyServer.class);
            server.config(configHolder);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    server.shutdown();
                } catch (Exception e) {
                    log.error("Problem occurred when stopping server: ", e);
                } finally {
                    log.info("Bye");
                }
            }));
            server.run();
        } catch (Exception e) {
            log.error("There was a problem running server: ", e);
        }
    }
}
