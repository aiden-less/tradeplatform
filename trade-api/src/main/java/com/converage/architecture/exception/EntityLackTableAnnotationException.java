package com.converage.architecture.exception;

public class EntityLackTableAnnotationException extends Exception{
    public EntityLackTableAnnotationException(){
        super();
    }

    public EntityLackTableAnnotationException(String message){
        super(message);

    }
}
