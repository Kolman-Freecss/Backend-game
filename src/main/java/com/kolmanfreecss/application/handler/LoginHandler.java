package com.kolmanfreecss.application.handler;

import com.kolmanfreecss.application.service.LoginService;
import com.kolmanfreecss.domain.exception.HttpWrapperException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.xml.bind.DatatypeConverter;
import java.io.*;

public class LoginHandler implements HttpHandler {
    
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
                exchange.sendResponseHeaders(e.getHttpCode(), e.getMessage().getBytes().length);
            }
        } else if (requestMethod.equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException, HttpWrapperException {

        String[] path = exchange.getRequestURI().toString().split("/");
        if (path.length != 3 || !path[2].equals("login")) {
            throw new HttpWrapperException(404, "Not found");
        }
        
        String userId = exchange.getRequestURI().toString().split("/")[1];
        if (userId != null || userId.isEmpty()) {
            throw new HttpWrapperException(400, "User ID is required");
        }
        long userIdLong = 0L;
        try {
            userIdLong = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            throw new HttpWrapperException(400, "User ID must be a number");
        }

        long sessionId = this.loginService.login(userIdLong);
        if (sessionId != 0) {
            setSessionCookie(exchange, String.valueOf(sessionId));
            String response = "Session ID: " + sessionId;
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            PrintStream ps = new PrintStream(os);
            ps.print(response);
            ps.close();
        } else {
            exchange.sendResponseHeaders(401, -1);
        }
    }

    /**
     * Sets the session cookie in the response headers encoded in base64.
     */
    private void setSessionCookie(HttpExchange exchange, String sessionId) {
        String cookie = SESSION_COOKIE_NAME + "=" + sessionId;
        String encodedCookie = DatatypeConverter.printBase64Binary(cookie.getBytes());
        exchange.getResponseHeaders().add("Set-Cookie", SESSION_COOKIE_NAME + "=" + encodedCookie);
    }
    
}
