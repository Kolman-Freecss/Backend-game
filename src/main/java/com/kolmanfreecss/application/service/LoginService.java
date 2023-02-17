package com.kolmanfreecss.application.service;

import com.kolmanfreecss.domain.exception.HttpWrapperException;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LoginService {

    // 10 minutes 
    private static final int SESSION_LENGTH = 10 * 60 * 1000;
    private static final ConcurrentHashMap<String, Long> sessions = new ConcurrentHashMap<>();
    public static final Random random = new Random();

    /**
     * Login the user and return true if the login was successful.
     */
    public long login(long userId) throws HttpWrapperException {

        // TODO: Check username and password against database
//        if (userId != 4711L) {
//            throw new HttpLoginException(400, "Invalid userId");
//        }

        // TODO: Here we could and should encrypt the session id with a private key 
        long sessionId = UUID.randomUUID().getMostSignificantBits();
        sessions.put(String.valueOf(sessionId), System.currentTimeMillis());

        // Set timer to remove session after SESSION_LENGTH
        new Thread(() -> {
            try {
                Thread.sleep(SESSION_LENGTH);
                sessions.remove(String.valueOf(sessionId));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        return sessionId;
    }


}
