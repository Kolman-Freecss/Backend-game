package com.kolmanfreecss;

import com.kolmanfreecss.application.handler.RootHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            startServer();
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    public static void startServer() throws Exception {
        logger.info("Starting server...");
        HttpServer server = HttpServer.create(new InetSocketAddress(HOST, PORT), 0);

        server.createContext("/", new RootHandler());

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