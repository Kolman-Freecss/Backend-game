package com.kolmanfreecss.application.handler;

import com.kolmanfreecss.application.service.LoginService;
import com.kolmanfreecss.application.service.ScoreService;
import com.kolmanfreecss.domain.exception.HttpWrapperException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScoreHandler implements HttpHandler {

    Logger logger = Logger.getLogger(ScoreHandler.class.getName());
    
    private final ScoreService scoreService;
    private final LoginService loginService;

    public ScoreHandler() {
        this.scoreService = new ScoreService();
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
            try {
                handleGet(exchange);
            } catch (HttpWrapperException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                exchange.sendResponseHeaders(e.getHttpCode(), e.getMessage().getBytes().length);
            }
        }
    }

    private void handleGet(HttpExchange exchange) throws HttpWrapperException {
        String[] path = exchange.getRequestURI().toString().split("/");
        if (path[2].equals("highscorelist")) {
            try {
                getHighScoreList(exchange);
            } catch (IOException e) {
                HttpWrapperException ex = new HttpWrapperException(500, "Internal server error");
                logger.log(Level.SEVERE, ex.getMessage(), ex);
                throw ex;
            }
        } else {
            HttpWrapperException ex = new HttpWrapperException(404, "Not found");
            logger.log(Level.WARNING, ex.getMessage(), ex);
            throw ex;
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException, HttpWrapperException {
        String[] path = exchange.getRequestURI().toString().split("/");
        if (path.length == 3 || path[2].startsWith("score")) {
            postScore(exchange);
        } else {
            throw new HttpWrapperException(404, "Not found");
        }
    }

    /**
     * Handles the GET /{levelid}/highscorelist request for the high score list.
     */
    private void getHighScoreList(HttpExchange exchange) throws HttpWrapperException, IOException {

        String levelId = exchange.getRequestURI().toString().split("/")[1];
        if (levelId == null || levelId.isEmpty()) {
            HttpWrapperException ex = new HttpWrapperException(400, "Level ID is required");
            logger.log(Level.WARNING, ex.getMessage(), ex);
            throw ex;
        }

        long levelIdLong = 0L;
        try {
            levelIdLong = Long.parseLong(levelId);
        } catch (NumberFormatException e) {
            HttpWrapperException ex = new HttpWrapperException(400, "User ID must be a number");
            logger.log(Level.WARNING, ex.getMessage(), ex);
            throw ex;
        }

        String scoreList = this.scoreService.getHighScoreList(levelIdLong);
        exchange.sendResponseHeaders(200, scoreList.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        PrintStream ps = new PrintStream(os);
        ps.print(scoreList);
        ps.close();
        logger.log(Level.INFO, () -> "High score list sent, levelId: " + levelId);
    }

    /**
     * Handles the POST /{levelid}/score request for saving a score.
     */
    private void postScore(HttpExchange exchange) throws HttpWrapperException, IOException {
        String token = exchange.getRequestURI().getQuery().split("=")[1];
        if (token == null || token.isEmpty()) {
            HttpWrapperException ex = new HttpWrapperException(400, "Session ID is required");
            logger.log(Level.WARNING, ex.getMessage(), ex);
            throw ex;
        }

        String sessionId = "";
        try {
            sessionId = this.loginService.isValidSessionToken(token);
            if (sessionId == null || sessionId.isEmpty()) {
                throw new HttpWrapperException(401, "Invalid session ID");
            }
        } catch (Exception e) {
            HttpWrapperException ex = new HttpWrapperException(401, e.getMessage());
            logger.log(Level.WARNING, e.getMessage(), ex);
            throw ex;
        }

        long userId = 0L;
        try {
            userId = this.loginService.getUserIdFromSessionId(sessionId);
        } catch (IllegalArgumentException e) {
            HttpWrapperException ex = new HttpWrapperException(401, e.getMessage());
            logger.log(Level.WARNING, e.getMessage(), ex);
            throw ex;
        }

        String levelId = exchange.getRequestURI().toString().split("/")[1];
        if (levelId == null || levelId.isEmpty()) {
            HttpWrapperException ex = new HttpWrapperException(400, "User ID is required");
            logger.log(Level.WARNING, ex.getMessage(), ex);
            throw ex;
        }

        long levelIdLong = 0L;
        try {
            levelIdLong = Long.parseLong(levelId);
        } catch (NumberFormatException e) {
            HttpWrapperException ex = new HttpWrapperException(400, "User ID must be a number");
            logger.log(Level.WARNING, ex.getMessage(), ex);
            throw ex;
        }

        try {
            JsonObject jsonObject = getJsonObjectFromRequestBody(exchange);
            long score = Long.parseLong(jsonObject.get("score").toString());
            this.scoreService.saveScore(levelIdLong, score, userId);
            logger.log(Level.INFO, () -> "Score saved, levelId: " + levelId + ", score: " + score);
            exchange.sendResponseHeaders(200, 0);
        } catch (Exception e) {
            HttpWrapperException ex = new HttpWrapperException(400, e.getMessage());
            logger.log(Level.WARNING, e.getMessage(), ex);
            throw ex;
        }

    }

    /**
     * Gets the JSON object from the request body.
     */
    private JsonObject getJsonObjectFromRequestBody(HttpExchange exchange) throws IOException, HttpWrapperException {
        // Read the request body
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        // Read all the lines of the request body
        StringBuilder sb = new StringBuilder();
        String json = "";
        while ((json = br.readLine()) != null) {
            sb.append(json);
        }
        JsonObject jsonObject = null;
        try (JsonReader jsonReader = Json.createReader(new StringReader(sb.toString()))) {
            jsonObject = jsonReader.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (jsonObject == null) {
            throw new HttpWrapperException(400, "Invalid username or password");
        }
        return jsonObject;
    }

}
