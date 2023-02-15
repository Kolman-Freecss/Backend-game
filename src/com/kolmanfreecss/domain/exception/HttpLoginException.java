package com.kolmanfreecss.domain.exception;

/**
 * Exception thrown when a login fails due to an HTTP error code.
 * */
public class HttpLoginException extends Exception{
    
    private final int httpCode;
    
    public HttpLoginException(int code, String message) {
        super(message);
        this.httpCode = code;
    }
    
    public int getHttpCode() {
        return httpCode;
    }
}
