package com.converage.exception;

public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = -2851132677704481659L;

    public BusinessException(){
        super();
    }

    public BusinessException(String message){
        super(message);
    }
}
