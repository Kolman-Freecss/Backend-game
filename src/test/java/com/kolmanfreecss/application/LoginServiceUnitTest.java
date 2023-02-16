package com.kolmanfreecss.application;

import com.kolmanfreecss.application.service.LoginService;
import com.kolmanfreecss.domain.exception.HttpLoginException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class LoginServiceUnitTest {
    
    private LoginService loginService;

    @BeforeEach
    public void setUp() {
        System.out.println("TEST LoginServiceUnitTest: Setup");
        loginService = Mockito.mock(LoginService.class);
    }
    
    @Test
    void login() throws HttpLoginException {
        System.out.println("TEST LoginServiceUnitTest: Login");
        Mockito.when(loginService.login("kolman-freecss", "warcraft")).thenReturn(1L);
        long sessionId = loginService.login("kolman-freecss", "warcraft");
        Assertions.assertEquals(1L, sessionId);
    }
    
}
