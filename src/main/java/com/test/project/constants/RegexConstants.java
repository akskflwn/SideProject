package com.test.project.constants;

public class RegexConstants {
    /**
     *  PASSWORD 는 영어, 숫자, 특수문자를 섞어서
     * */

    public static final String PHONE = "^(01[016789]\\d{3,4}\\d{4})$";
    public static final String PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,20}$";
}