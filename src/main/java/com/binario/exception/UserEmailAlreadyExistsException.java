package com.binario.exception;

public class UserEmailAlreadyExistsException extends RuntimeException {
    public UserEmailAlreadyExistsException() { super(); }
    public UserEmailAlreadyExistsException(String message) {
        super(message);
    }
}
