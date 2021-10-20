package com.jinyframework.keva.server;

import com.jinyframework.keva.proxy.config.ConfigHolder;
import com.jinyframework.keva.proxy.core.NettyServer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.SocketClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class ProxyTest {
    static String host = "localhost";
    static String shard1Name = "Shard1";
    static String shard2Name = "Shard2";
    static SocketClient client;
    static AtomicBoolean isServerRunning = new AtomicBoolean(true);
    static NettyServer server;

    @BeforeAll
    static void setup() throws IOException, InterruptedException {
        startServer(6969, shard1Name);
        startServer(6970, shard2Name);
        server = new NettyServer(ConfigHolder.defaultBuilder()
                .hostname(host)
                .port(6971)
                .virtualNodeAmount(3)
                .serverList(host + ":" + 6969 + "," + host + ":" + 6970)
                .build());

        new Thread(() -> {
            try {
                server.run();
            } catch (Exception e) {
                log.error(e.getMessage());
                System.exit(1);
            }
        }).start();

        // Wait for server to start
        TimeUnit.SECONDS.sleep(20);

        client = new SocketClient(host, 6971);
        client.connect();
    }

    @AfterAll
    static void stop() throws IOException {
        client.disconnect();
        server.shutdown();
        isServerRunning.set(false);
    }

    public static void startServer(int port, String name) {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

        Runnable serverTask = () -> {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                Socket clientSocket = serverSocket.accept();
                clientProcessingPool.submit(new ClientTask(clientSocket, name));
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Unable to process client request");
                e.printStackTrace();
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }

    @AllArgsConstructor
    private static class ClientTask implements Runnable {
        private final Socket clientSocket;
        private final String serverName;

        @SneakyThrows
        @Override
        public void run() {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
            while (isServerRunning.get()) {
                if (reader.ready()) {
                    String line = reader.readLine();
                    writer.println(line + serverName);
                    writer.flush();
                }
            }
            clientSocket.close();
        }
    }

    @Test
    void ping() {
        try {
            String msg = client.exchange("ping");
            assertEquals("PONG", msg);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void info() {
        try {
            val info = client.exchange("info");
            assertFalse("null".contentEquals(info));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void forward() {
        try {
            val firstCommand = "get 1";
            val secondCommand = "get 5";
            val shard1Msg = client.exchange(firstCommand);
            val shard2Msg = client.exchange(secondCommand);
            assertEquals(firstCommand + shard2Name, shard1Msg);
            assertEquals(secondCommand + shard1Name, shard2Msg);
        } catch (Exception e) {
            fail(e);
        }
    }
}
