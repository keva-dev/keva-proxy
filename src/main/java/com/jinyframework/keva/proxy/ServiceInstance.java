package com.jinyframework.keva.proxy;

import com.jinyframework.keva.proxy.balance.LoadBalancingService;
import com.jinyframework.keva.proxy.balance.LoadBalancingServiceImpl;
import com.jinyframework.keva.proxy.command.CommandService;
import com.jinyframework.keva.proxy.command.CommandServiceImpl;
import com.jinyframework.keva.proxy.util.ConnectionService;
import com.jinyframework.keva.proxy.util.ConnectionServiceDefaultImpl;
import lombok.Setter;

@Setter
public final class ServiceInstance {
    private ServiceInstance() {
    }

    public static ConnectionService getConnectionService() {
        return ConnectionServiceHolder.INSTANCE;
    }

    public static CommandService getCommandService() {
        return CommandServiceHolder.INSTANCE;
    }

    public static LoadBalancingService getLoadBalancingService() {
        return LoadBalancingServiceHolder.INSTANCE;
    }

    private static final class ConnectionServiceHolder {
        private static final ConnectionService INSTANCE = new ConnectionServiceDefaultImpl();
    }

    private static final class CommandServiceHolder {
        private static final CommandService INSTANCE = new CommandServiceImpl();
    }

    private static final class LoadBalancingServiceHolder {
        private static final LoadBalancingService INSTANCE = new LoadBalancingServiceImpl();
    }
}
