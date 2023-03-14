package com.itzblaze.modulewithasm;

import com.sun.net.httpserver.HttpServer;
import net.minecraft.client.Minecraft;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ProxySwitchListener {

    private int port = 999999;

    public Socket s;

    public ProxySwitchListener() {

    }

    private String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(input.getBytes("UTF-8"));

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    public void start() throws IOException {
        Minecraft mc = Minecraft.getMinecraft();

        int nonce = 0;
        while(port >= 65352) {
            nonce++;
            String hash = hash(mc.getSession().getUsername() + nonce);
            System.out.println(hash);
            StringBuilder b = new StringBuilder();
            int j = 0;
            for (char c : hash.toCharArray()) {
                if (j > 4) break;
                if (Character.isDigit(c)) {
                    j++;
                    b.append(c);
                }
            }
            port = Integer.parseInt(b.toString());
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpServer server = null;
                try {
                    server = HttpServer.create(new InetSocketAddress(port),0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                server.createContext("/proxies", new MyHandler());
                server.setExecutor(null);
                server.start();
            }
        });
        t.start();

    }

    static class MyHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            System.out.println("recieved");
            Map<String, String> params = queryToMap(t.getRequestURI().getQuery());
            String response = "failed";
            String address = params.get("address");
            int port = Integer.parseInt(params.get("port"));
            String user = params.get("user");
            String pass = params.get("pass");
            TestPing testPing = null;
            testPing = new TestPing();
            testPing.run("mc.hypixel.net", 25565, ProxyServer.proxy.ip);
            ProxyServer.proxy.enabled = Boolean.parseBoolean(params.get("enabled"));
            ProxyServer.proxy.ip = address;
            ProxyServer.proxy.port = port;
            ProxyServer.proxy.username = user;
            ProxyServer.proxy.password = pass;
            ProxyServer.proxy.type = ProxyServer.ProxyType.SOCKS5;
            System.out.println(user + ":" + pass);
            Config.saveProxy(ProxyServer.proxy);
            Config.loadCurrentProxy();
        }
    }

}
