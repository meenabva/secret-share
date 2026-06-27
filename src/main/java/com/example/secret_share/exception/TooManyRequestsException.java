package com.example.secret_share.exception;

public class TooManyRequestsException extends RuntimeException {

    public TooManyRequestsException(String message){
        super(message);
    }
}
