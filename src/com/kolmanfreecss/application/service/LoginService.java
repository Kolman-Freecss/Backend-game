package com.kolmanfreecss.application.service;

import com.kolmanfreecss.domain.exception.HttpLoginException;
import com.sun.net.httpserver.HttpExchange;

import javax.xml.bind.DatatypeConverter;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class LoginService {

    // 10 minutes 
    private static final int SESSION_LENGTH = 10 * 60 * 1000;
    private static final String SESSION_COOKIE_NAME = "SESSION_ID";
    private static final ConcurrentHashMap<String, Long> sessions = new ConcurrentHashMap<>();
    public static final Random random = new Random();

    /**
     * Login the user and return true if the login was successful.
     */
    public String login(HttpExchange exchange) throws HttpLoginException{
        String username = exchange.getRequestHeaders().getFirst("username");
        String password = exchange.getRequestHeaders().getFirst("password");
        
        if (username == null || password == null
                || username.isEmpty() || password.isEmpty()) {
            return null;
        }
        
        // TODO: Check username and password against database
        if (!username.equals("admin") || !password.equals("admin")) {
            throw new HttpLoginException(400, "Invalid username or password");
        }
        
        long sessionId = random.nextLong();
        sessions.put(String.valueOf(sessionId), System.currentTimeMillis());
        setSessionCookie(exchange, String.valueOf(sessionId));
        
        String response = "Session ID: " + sessionId;
        
        // Set timer to remove session after SESSION_LENGTH
        new Thread(() -> {
            try {
                Thread.sleep(SESSION_LENGTH);
                sessions.remove(String.valueOf(sessionId));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        
        return response;
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
