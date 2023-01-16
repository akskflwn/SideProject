package com.test.project.exception.user;

public class UserNotMatchException extends RuntimeException {

    public UserNotMatchException(String message) {
        super(message);
    }
}
