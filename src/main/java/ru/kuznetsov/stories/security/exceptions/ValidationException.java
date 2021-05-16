package ru.kuznetsov.stories.security.exceptions;

public class ValidationException extends RuntimeException {
    public ValidationException(String msg){
        super(msg);
    }
}
