package com.jinyframework.keva.proxy.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConnectionServiceDefaultImpl implements ConnectionService {
	private final ConcurrentHashMap<String, connection.ClientInfo> clients = new ConcurrentHashMap<>();

	@Override
	public long getCurrentConnectedClients() {
		return clients.size();
	}

	@Override
	public ConcurrentMap<String, connection.ClientInfo> getClients() {
		return clients;
	}
}
