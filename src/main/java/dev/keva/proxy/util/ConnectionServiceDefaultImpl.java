package dev.keva.proxy.util;

import dev.keva.ioc.annotation.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ConnectionServiceDefaultImpl implements ConnectionService {
	private final ConcurrentHashMap<String, ClientInfo> clients = new ConcurrentHashMap<>();

	@Override
	public long getCurrentConnectedClients() {
		return clients.size();
	}

	@Override
	public ConcurrentMap<String, ClientInfo> getClients() {
		return clients;
	}
}
