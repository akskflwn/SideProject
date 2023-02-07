package com.test.project.util.encrption;

public interface EncryptionService {
    public String encrypt(String rawPassword);

    public boolean isSamePassword(String rawPassword, String encodedPassword);
}