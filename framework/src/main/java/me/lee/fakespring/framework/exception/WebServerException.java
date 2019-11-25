package me.lee.fakespring.framework.exception;

public class WebServerException extends RuntimeException {

    public WebServerException() {}

    public WebServerException(String message) {
        super(message);
    }

}
