package com.converage.architecture.exception;

public class LoginException extends RuntimeException {
    public LoginException(){
        super();
    }

    public LoginException(String message){
        super(message);
    }
}