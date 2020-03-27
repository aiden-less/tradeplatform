package com.converage.exception;

public class WalletException extends RuntimeException {

    private static final long serialVersionUID = 7498686639470976535L;

    public WalletException(){
        super();
    }

    public WalletException(String message){
        super(message);
    }
}
