package com.test.project.exception.board;

public class ReplyNotFoundException extends RuntimeException {

    public ReplyNotFoundException(String message) {
        super(message);
    }
}
