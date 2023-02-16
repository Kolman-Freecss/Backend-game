package com.kolmanfreecss;

import com.kolmanfreecss.application.controller.LoginController;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

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
        server.createContext("/login", new LoginController());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + PORT);
    }
}