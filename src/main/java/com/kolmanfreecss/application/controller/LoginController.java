package com.kolmanfreecss.application.controller;

import com.kolmanfreecss.application.service.LoginService;
import com.kolmanfreecss.domain.exception.HttpLoginException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class LoginController implements HttpHandler {

    private static final String SESSION_COOKIE_NAME = "SESSION_ID";

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
            exchange.sendResponseHeaders(405, -1);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException, HttpLoginException {
        LoginService loginService = new LoginService();
        try {
            JsonObject jsonObject = getJsonObjectFromRequestBody(exchange);

            String username = jsonObject.getString("username");
            String password = jsonObject.getString("password");
            
            long sessionId = loginService.login(username, password);
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
        } catch (HttpLoginException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw e;
        }
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

    /**
     * Sets the session cookie in the response headers encoded in base64.
     */
    private void setSessionCookie(HttpExchange exchange, String sessionId) {
        String cookie = SESSION_COOKIE_NAME + "=" + sessionId;
        String encodedCookie = DatatypeConverter.printBase64Binary(cookie.getBytes());
        exchange.getResponseHeaders().add("Set-Cookie", SESSION_COOKIE_NAME + "=" + encodedCookie);
    }
    
}
