package com.kolmanfreecss.application;

import com.kolmanfreecss.application.service.LoginService;
import com.kolmanfreecss.domain.exception.HttpWrapperException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.logging.Logger;

class LoginServiceUnitTest {
    
    Logger logger = Logger.getLogger(LoginServiceUnitTest.class.getName());
    
    private LoginService loginService;

    @BeforeEach
    public void setUp() {
        logger.info("TEST LoginServiceUnitTest: Setup");
        loginService = Mockito.mock(LoginService.class);
    }
    
    @Test
    void login() throws HttpWrapperException {
        logger.info("TEST LoginServiceUnitTest: Login");
        Mockito.when(loginService.login(4L)).thenReturn("SessionId");
        String sessionId = loginService.login(4L);
        Assertions.assertEquals("SessionId", sessionId);
    }
    
}
