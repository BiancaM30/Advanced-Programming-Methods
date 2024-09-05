package com.example.toysocialnetworkgui.validators;


public class ServiceException extends RuntimeException{
    public ServiceException(){

    }
    public ServiceException(String message){
        super(message);
    }
}

