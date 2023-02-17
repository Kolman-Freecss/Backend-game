package com.kolmanfreecss.application.service;

import com.kolmanfreecss.domain.exception.HttpWrapperException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.logging.Logger;

//TODO: This test should be through the H2 database.
class ScoreServiceUnitTest {
    
    Logger logger = Logger.getLogger(ScoreServiceUnitTest.class.getName());
    
    private ScoreService scoreService;
    
    @BeforeEach
    public void setUp() throws URISyntaxException {
        logger.info("TEST ScoreServiceUnitTest: Setup");
        scoreService = new ScoreService();
        scoreService.saveScore(4L, 100L, 7L);
        scoreService.saveScore(4L, 200L, 8L);
        scoreService.saveScore(4L, 300L, 9L);
    }
    
    @Test
    void getHighScoreList() throws HttpWrapperException {
        logger.info("TEST ScoreServiceUnitTest: Get High Score List");
        
        Assertions.assertEquals("7=100,8=200,9=300", scoreService.getHighScoreList(4L));
    }
    
}
