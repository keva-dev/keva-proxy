package dev.keva.proxy.command;

import java.util.HashMap;
import java.util.Map;

import dev.keva.ioc.annotation.Autowired;
import dev.keva.ioc.annotation.Component;

@Component
public final class CommandRegistrar {

    private final Map<CommandName, CommandHandler> registrar;

    @Autowired
    public CommandRegistrar(Info infoService, Forward forward) {
        registrar = new HashMap<>();
        registrar.put(CommandName.GET, forward);
        registrar.put(CommandName.SET, forward);
        registrar.put(CommandName.PING, new Ping());
        registrar.put(CommandName.INFO, infoService);
        registrar.put(CommandName.DEL, forward);
        registrar.put(CommandName.EXPIRE, forward);
        registrar.put(CommandName.FSYNC, forward);
        registrar.put(CommandName.UNSUPPORTED, new Unsupported());
    }

    public Map<CommandName, CommandHandler> getHandlerMap() {
        return registrar;
    }
}
