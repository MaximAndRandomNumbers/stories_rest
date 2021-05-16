package ru.kuznetsov.stories.services.register_and_auth;

public class VerificationException extends RuntimeException {

    public VerificationException(String message) {
        super(message);
    }
}
