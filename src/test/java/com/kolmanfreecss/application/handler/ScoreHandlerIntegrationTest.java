package com.kolmanfreecss.application.handler;

import com.kolmanfreecss.domain.exception.HttpWrapperException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

// TODO: Here we could use the H2 database to test the database connection.
class ScoreHandlerIntegrationTest {

    Logger logger = Logger.getLogger(ScoreHandlerIntegrationTest.class.getName());

    private HttpExchange exchange;
    private ScoreHandler handler;

    @BeforeEach
    public void setUp() {
        logger.info("TEST ScoreServiceUnitTest: Setup");

        exchange = Mockito.mock(HttpExchange.class);

        Headers requestHeaders = new Headers();
        requestHeaders.add("Content-Type", "application/json");

        Mockito.when(exchange.getRequestHeaders()).thenReturn(requestHeaders);

        handler = new ScoreHandler();
    }

    /**
     * Test the saveScore method.
     */
    @Test
    void postScore() throws HttpWrapperException, IOException {
        logger.info("TEST ScoreServiceUnitTest: Login");

        URI requestURI = null;

        try {
            requestURI = new URI("/4/highscorelist");
        } catch (URISyntaxException e) {
            logger.severe("TEST ScoreServiceUnitTest: Login: " + e.getMessage());
        }

        Mockito.when(exchange.getRequestURI()).thenReturn(requestURI);
        Mockito.when(exchange.getRequestMethod()).thenReturn("POST");

        handler.handle(exchange);

        Mockito.verify(exchange).sendResponseHeaders(404, "Not Found".length());
    }

}
