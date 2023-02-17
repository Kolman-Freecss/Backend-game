package com.kolmanfreecss.application.handler;

import com.kolmanfreecss.application.service.ScoreService;
import com.kolmanfreecss.domain.exception.HttpWrapperException;
import com.kolmanfreecss.domain.model.repository.ScoreRepository;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class ScoreHandler implements HttpHandler {

    private final ScoreService scoreService;

    public ScoreHandler() {
        this.scoreService = new ScoreService();
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
            exchange.sendResponseHeaders(200, -1);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException, HttpWrapperException {
        String[] path = exchange.getRequestURI().toString().split("/");
        if (path.length != 3 || !path[2].equals("score")) {
            throw new HttpWrapperException(404, "Not found");
        }
        
        String levelId = exchange.getRequestURI().toString().split("/")[1];
        if (levelId != null || levelId.isEmpty()) {
            throw new HttpWrapperException(400, "User ID is required");
        }
        long levelIdLong = 0L;
        try {
            levelIdLong = Long.parseLong(levelId);
        } catch (NumberFormatException e) {
            throw new HttpWrapperException(400, "User ID must be a number");
        }

        JsonObject jsonObject = getJsonObjectFromRequestBody(exchange);
        long score = jsonObject.getJsonNumber("score").longValue();
        this.scoreService.saveScore(levelIdLong, score);
        
        System.out.println("Score saved, levelId: " + levelId + ", score: " + score);
    }

    /**
     * Gets the JSON object from the request body.
     * */
    private JsonObject getJsonObjectFromRequestBody(HttpExchange exchange) throws IOException, HttpLoginException {
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
            throw new HttpLoginException(400, "Invalid username or password");
        }
        return jsonObject;
    }

}
