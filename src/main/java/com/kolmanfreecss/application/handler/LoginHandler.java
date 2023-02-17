package com.kolmanfreecss.application.handler;

import com.kolmanfreecss.application.service.LoginService;
import com.kolmanfreecss.domain.exception.HttpWrapperException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginHandler implements HttpHandler {
    
    Logger logger = Logger.getLogger(LoginHandler.class.getName());
    
    private static final String SESSION_COOKIE_NAME = "SESSION_ID";
    private final LoginService loginService;
    
    public LoginHandler() {
        this.loginService = new LoginService();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        if (requestMethod.equalsIgnoreCase("POST")) {
            try {
                handlePost(exchange);
            } catch (HttpWrapperException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                exchange.sendResponseHeaders(e.getHttpCode(), e.getMessage().getBytes().length);
            }
        } else if (requestMethod.equalsIgnoreCase("GET")) {
            logger.log(Level.INFO, "Not GET Request Method on LoginHandler");
            exchange.sendResponseHeaders(405, -1);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException, HttpWrapperException {
        String[] path = exchange.getRequestURI().toString().split("/");
        if (path[2].equals("login")) {
            login(exchange);
        } else {
            HttpWrapperException e = new HttpWrapperException(404, "Not found");
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Handles the POST /{userId}/login request.
     */
    private void login(HttpExchange exchange) throws IOException, HttpWrapperException {
        String userId = exchange.getRequestURI().toString().split("/")[1];
        if (userId == null || userId.isEmpty()) {
            HttpWrapperException e = new HttpWrapperException(404, "User ID is required");
            logger.log(Level.WARNING, e.getMessage(), e);
            throw e;
        }
        long userIdLong = 0L;
        try {
            userIdLong = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            HttpWrapperException ex = new HttpWrapperException(404, "User ID must be a number");
            logger.log(Level.WARNING, e.getMessage(), ex);
            throw ex;
        }

        String sessionId = this.loginService.login(userIdLong);
        if (sessionId != null && !sessionId.isEmpty()) {
            setSessionCookie(exchange, sessionId);
            String response = "Session ID: " + sessionId;
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            PrintStream ps = new PrintStream(os);
            ps.print(response);
            ps.close();
            logger.log(Level.INFO, () -> "Login successful -> " + response);
        } else {
            logger.log(Level.WARNING, "Session ID is invalid");
            exchange.sendResponseHeaders(401, -1);
        }
    }

    /**
     * Sets the session cookie in the response headers encoded in base64.
     */
    private void setSessionCookie(HttpExchange exchange, String sessionId) {
        String cookie = SESSION_COOKIE_NAME + "=" + sessionId;
        exchange.getResponseHeaders().add("Set-Cookie", SESSION_COOKIE_NAME + "=" + cookie);
    }
    
}
