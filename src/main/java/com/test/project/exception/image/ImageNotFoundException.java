package com.test.project.exception.image;

public class ImageNotFoundException extends NullPointerException {

    public ImageNotFoundException() {
    }

    public ImageNotFoundException(String message) {
        super(message);
    }
}