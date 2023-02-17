package com.kolmanfreecss.application;

import com.kolmanfreecss.application.service.LoginService;
import com.kolmanfreecss.application.service.ScoreService;
import com.kolmanfreecss.domain.exception.HttpWrapperException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.logging.Logger;

class ScoreServiceUnitTest {
    
    Logger logger = Logger.getLogger(ScoreServiceUnitTest.class.getName());
    
    private ScoreService scoreService;

    @BeforeEach
    public void setUp() {
        logger.info("TEST ScoreServiceUnitTest: Setup");
        scoreService = Mockito.mock(ScoreService.class);
    }
    
    /**
     * Test the saveScore method.
     * */
//    @Test
//    void saveScore() throws HttpWrapperException {
//        logger.info("TEST ScoreServiceUnitTest: Login");
//        Mockito.when(scoreService.saveScore(4L, 100L, 7L)).thenReturn("SessionId");
//        Assertions.assertEquals("SessionId", sessionId);
//    }
    
}
