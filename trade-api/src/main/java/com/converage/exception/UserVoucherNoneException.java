package com.converage.exception;

public class UserVoucherNoneException extends RuntimeException {
    public UserVoucherNoneException(){
        super();
    }

    public UserVoucherNoneException(String message){
        super(message);
    }
}