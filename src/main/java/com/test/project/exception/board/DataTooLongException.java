package com.test.project.exception.board;

public class DataTooLongException extends RuntimeException {
    public DataTooLongException(String message) {
        super(message);
    }
}