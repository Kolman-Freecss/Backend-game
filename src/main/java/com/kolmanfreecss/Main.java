package com.kolmanfreecss;

import com.kolmanfreecss.application.handler.LoginHandler;
import com.kolmanfreecss.application.handler.RootHandler;
import com.kolmanfreecss.application.handler.ScoreHandler;
import com.kolmanfreecss.application.service.LoginService;
import com.kolmanfreecss.application.service.ScoreService;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class Main {
    
    private static final int PORT = 8080;
    private static final String HOST = "localhost";
    
    public static void main(String[] args) {
        try {
            startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void startServer() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(HOST, PORT), 0);

        ConcurrentHashMap<String, HttpHandler> handlers = new ConcurrentHashMap<>();
        handlers.put("login", new LoginHandler());
        handlers.put("score", new ScoreHandler());
        
        server.createContext("/", new RootHandler(handlers));
        
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Server started on port " + PORT);
    }
}