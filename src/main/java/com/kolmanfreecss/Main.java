package com.kolmanfreecss;

import com.kolmanfreecss.application.handler.LoginHandler;
import com.kolmanfreecss.application.handler.RootHandler;
import com.kolmanfreecss.application.handler.ScoreHandler;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Main {
    
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    
    private static final int PORT = 8080;
    private static final String HOST = "localhost";
    
    public static void main(String[] args) {
        try {
            startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        String sessionId = UUID.randomUUID().getMostSignificantBits() + "-" + 7;
//        System.out.println("sessionId: " + sessionId);
//        String sessionIdBase64 = Base64.getEncoder().encodeToString(sessionId.getBytes(StandardCharsets.UTF_8));
//        System.out.println("sessionId codificada en Base64: " + sessionIdBase64);
//
//        byte[] decodedBytes = Base64.getDecoder().decode(sessionIdBase64);
//        String decodedSessionId = new String(decodedBytes, StandardCharsets.UTF_8);
//        System.out.println("sessionId decodificada: " + decodedSessionId);

    }
    
    public static void startServer() throws Exception {
        logger.info("Starting server...");
        HttpServer server = HttpServer.create(new InetSocketAddress(HOST, PORT), 0);

        ConcurrentHashMap<String, HttpHandler> handlers = new ConcurrentHashMap<>();
        handlers.put("login", new LoginHandler());
        handlers.put("score", new ScoreHandler());
        handlers.put("highscorelist", new ScoreHandler());
        
        server.createContext("/", new RootHandler(handlers));
        
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        logger.info("Server started on port " + PORT);
        logger.info("#############################################");
        logger.info("##                                         ##");
        logger.info("##  Welcome to Kolman-Freecss's server!    ##");
        logger.info("##                                         ##");
        logger.info("#############################################");
    }
}