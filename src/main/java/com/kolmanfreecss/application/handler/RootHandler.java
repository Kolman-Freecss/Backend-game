package com.kolmanfreecss.application.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class RootHandler implements HttpHandler {

    private static final ConcurrentHashMap<String, HttpHandler> handlers;

    static {
        // Initialize handlers
        handlers = new ConcurrentHashMap<>();
        handlers.put("login", new LoginHandler());
        handlers.put("score", new ScoreHandler());
        handlers.put("highscorelist", new ScoreHandler());
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String requestURI = exchange.getRequestURI().getPath();
        if (requestMethod.equalsIgnoreCase("GET") || requestMethod.equalsIgnoreCase("POST")) {
            HttpHandler handler = handlers.get(getPathHandler(requestURI));
            if (handler != null) {
                handler.handle(exchange);
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        } else {
            exchange.sendResponseHeaders(404, -1);
        }
    }

    private String getPathHandler(String requestURI) {
        String[] path = requestURI.split("/");
        return path[path.length - 1];
    }

}
