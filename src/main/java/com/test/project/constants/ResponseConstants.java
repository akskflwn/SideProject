package com.test.project.constants;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseConstants {
    public static final ResponseEntity<Void> OK =
        ResponseEntity.ok().build();

    public static final ResponseEntity<Void> CREATED =
        ResponseEntity.status(HttpStatus.CREATED).build();

    public static final ResponseEntity<Void> BAD_REQUEST =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    public static final ResponseEntity<String> DUPLICATED_EMAIL =
        new ResponseEntity<>("이미 등록된 이메일입니다.", HttpStatus.CONFLICT);

    public static final ResponseEntity<String> DUPLICATED_NICKNAME =
        new ResponseEntity<>("이미 등록된 닉네임입니다.", HttpStatus.CONFLICT);

    public static final ResponseEntity<String> USER_NOT_FOUND =
        new ResponseEntity<>("이메일 혹은 비밀번호를 잘못 입력하였습니다.", HttpStatus.NOT_FOUND);

    public static final ResponseEntity<String> WRONG_PASSWORD =
        new ResponseEntity<>("잘못된 비밀번호를 입력하였습니다.", HttpStatus.UNAUTHORIZED);

    public static final ResponseEntity<String> ALREADY_MY_NICKNAME =
        new ResponseEntity<>("이미 내가 사용중인 닉네임입니다.", HttpStatus.CONFLICT);

    public static final ResponseEntity<String> ALREADY_MY_PASSWORD =
        new ResponseEntity<>("이미 내가 사용중인 비밀번호입니다.", HttpStatus.CONFLICT);

    public static final ResponseEntity<String> TOKEN_EXPIRED =
        new ResponseEntity<>("Access 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED);

    public static final ResponseEntity<String> REFRESH_TOKEN_EXPIRED =
        new ResponseEntity<>("Refresh 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED);

    public static final ResponseEntity<String> NOT_LOGINED_USER =
        new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.NOT_FOUND);
    public static final ResponseEntity<String> WRONG_EMAIL_OR_NAME =
        new ResponseEntity<>("이메일 혹은 이름을 잘못 입력했습니다.", HttpStatus.NOT_FOUND);

    public static final ResponseEntity<String> FAILED_IMAGE_CONVERT =
        new ResponseEntity<>("이미지 파일 변환에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    public static final ResponseEntity<String> FAILED_IMAGE_UPLOAD =
        new ResponseEntity<>("이미지 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    public static final ResponseEntity<String> IMAGE_NOT_FOUND =
        new ResponseEntity<>("이미지 파일을 찾는데 실패했습니다.", HttpStatus.NOT_FOUND);


}
