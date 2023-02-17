package com.kolmanfreecss.application.service;

import com.kolmanfreecss.domain.exception.HttpWrapperException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginService {

    Logger logger = Logger.getLogger(LoginService.class.getName());
    
    // 10 minutes 
    private static final int SESSION_LENGTH = 10 * 60 * 1000;
    private static final String SESSION_SECRET = "warcraft_secret_base64";
    private static final ConcurrentHashMap<String, Long> sessions = new ConcurrentHashMap<>();

    /**
     * Login the user and return true if the login was successful.
     */
    public String login(long userId) throws HttpWrapperException {

        String sessionId = UUID.randomUUID().getMostSignificantBits() + "." + userId;
        
        logger.info(() -> "New Login: " + sessionId);
        
        sessions.put(sessionId, System.currentTimeMillis());

        // Set timer to remove session after SESSION_LENGTH
        new Thread(() -> {
            try {
                Thread.sleep(SESSION_LENGTH);
                sessions.remove(sessionId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        
        String token = "";
        try {
            token = generateSessionToken(sessionId);
        } catch (Exception e) {
            HttpWrapperException ex = new HttpWrapperException(500, "Internal server error");
            logger.log(Level.SEVERE, e.getMessage(), ex);
            throw ex;
        }

        return token;
    }
    
    private String generateSessionToken(String sessionId) {
        sessionId += "." + SESSION_SECRET;
        return Base64.getEncoder().encodeToString(sessionId.getBytes(StandardCharsets.UTF_8));
    }

    private String decryptSessionToken(String token) {
        return new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
    }
    
    /**
     * Get the user id from the session token.
     */
    public long getUserIdFromSessionId(String sessionId) throws IllegalArgumentException{
        String[] parts = sessionId.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid session id");
        }
        return Long.parseLong(parts[1]);
    }
    
    /**
     * Check if the session is valid and return true if it is.
     */
    public String isValidSessionToken(String token) {
        String decrypted = decryptSessionToken(token);
        String[] parts = decrypted.split("\\.");
        if (parts.length < 2) {
            return "";
        }
        String sessionId = parts[0] + "." + parts[1];
        return isValidSessionId(sessionId) ? sessionId : "";
    }

    /**
     * Check if the session is valid and return true if it is.
     */
    private boolean isValidSessionId(String sessionId) {
        return sessions.containsKey(sessionId);
    }

}
