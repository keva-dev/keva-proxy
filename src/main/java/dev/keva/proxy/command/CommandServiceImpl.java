package dev.keva.proxy.command;

import dev.keva.ioc.annotation.Autowired;
import dev.keva.ioc.annotation.Component;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
@Component
public class CommandServiceImpl implements CommandService {

    @Autowired
    private CommandRegistrar commandRegistrar;

    @Override
    public void handleCommand(ChannelHandlerContext ctx, String line) {
        try {
            val args = CommandService.parseTokens(line);
            CommandName command;
            try {
                command = CommandName.valueOf(args.get(0).toUpperCase());
            } catch (IllegalArgumentException e) {
                command = CommandName.UNSUPPORTED;
            }

            val handler =  commandRegistrar.getHandlerMap().get(command);
            handler.handle(ctx, line);
        } catch (Exception e) {
            log.error("Error while handling command: ", e);
        }
    }
}
