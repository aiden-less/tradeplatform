package com.converage.architecture.exception;

public class DataTypeErrorException extends Exception{
    public DataTypeErrorException(){
        super();
    }

    public DataTypeErrorException(String message){
        super(message);
    }
}
