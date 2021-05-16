package ru.kuznetsov.stories.security.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {
    private HttpStatus httpStatus;

    public JwtAuthenticationException(String msg, HttpStatus status) {
        super(msg);
        httpStatus = status;
    }

    public JwtAuthenticationException(String msg) {
        super(msg);
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
