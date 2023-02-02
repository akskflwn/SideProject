package com.test.project.exception.image;

public class FailedImageUploadException extends IllegalArgumentException {
    public FailedImageUploadException(String message) {
        super(message);
    }
}