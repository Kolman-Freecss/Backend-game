package com.kolmanfreecss.application.controller;

import com.kolmanfreecss.application.service.LoginService;
import com.kolmanfreecss.domain.exception.HttpLoginException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class LoginController implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        if (requestMethod.equalsIgnoreCase("POST")) {
            try {
                handlePost(exchange);
            } catch (HttpLoginException e) {
                exchange.sendResponseHeaders(e.getHttpCode(), e.getMessage().getBytes().length);
            }
        } else if (requestMethod.equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(200, -1);
        }
    }
    
    private void handlePost(HttpExchange exchange) throws IOException, HttpLoginException {
        LoginService loginService = new LoginService();
        try {
            String response = loginService.login(exchange);
            if (response != null) {
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                PrintStream ps = new PrintStream(os);
                ps.print(response);
                ps.close();
            } else {
                exchange.sendResponseHeaders(401, -1);
            }
        } catch (HttpLoginException e) {
            throw e;
        }
    }
}
