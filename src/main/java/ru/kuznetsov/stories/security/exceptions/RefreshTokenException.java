package ru.kuznetsov.stories.security.exceptions;


public class RefreshTokenException extends RuntimeException {

    public RefreshTokenException(String msg){
        super(msg);
    }
}
