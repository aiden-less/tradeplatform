package com.converage.exception;

public class CoinException extends RuntimeException {

    private static final long serialVersionUID = 7498686639470976535L;

    public CoinException(){
        super();
    }

    public CoinException(String message){
        super(message);
    }
}
