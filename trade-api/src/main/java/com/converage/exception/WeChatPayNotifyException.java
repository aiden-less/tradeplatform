package com.converage.exception;

public class WeChatPayNotifyException extends RuntimeException {
    public WeChatPayNotifyException(){
        super();
    }

    public WeChatPayNotifyException(String message){
        super(message);
    }
}