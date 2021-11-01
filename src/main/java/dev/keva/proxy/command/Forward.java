package dev.keva.proxy.command;

import dev.keva.ioc.annotation.Autowired;
import dev.keva.ioc.annotation.Component;
import dev.keva.proxy.balance.LoadBalancingService;
import dev.keva.proxy.balance.Request;
import io.netty.channel.ChannelHandlerContext;
import lombok.val;

@Component
public class Forward implements CommandHandler {

	private final LoadBalancingService loadBalancingService;

	@Autowired
	public Forward(LoadBalancingService loadBalancingService) {
		this.loadBalancingService = loadBalancingService;
	}

	@Override
	public void handle(ChannelHandlerContext ctx, String line) {
		val args = CommandService.parseTokens(line);
		loadBalancingService.forwardRequest(new Request(ctx, line), args.get(1));
	}
}
