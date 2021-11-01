package dev.keva.proxy.util;

import java.util.concurrent.ConcurrentMap;

public interface ConnectionService {
	long getCurrentConnectedClients();

	ConcurrentMap<String, ClientInfo> getClients();
}
