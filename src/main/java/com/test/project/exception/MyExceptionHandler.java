package com.test.project.exception;


import static com.test.project.constants.ResponseConstants.ALREADY_MY_PASSWORD;
import static com.test.project.constants.ResponseConstants.DUPLICATED_EMAIL;
import static com.test.project.constants.ResponseConstants.DUPLICATED_NICKNAME;
import static com.test.project.constants.ResponseConstants.NOT_LOGINED_USER;
import static com.test.project.constants.ResponseConstants.USER_NOT_FOUND;
import static com.test.project.constants.ResponseConstants.WRONG_EMAIL_OR_NAME;
import static com.test.project.constants.ResponseConstants.WRONG_PASSWORD;

import com.test.project.exception.user.AlreadyMyPasswordException;
import com.test.project.exception.user.DuplicatedEmailException;
import com.test.project.exception.user.DuplicatedNicknameException;
import com.test.project.exception.user.UserNotFoundException;
import com.test.project.exception.user.UserNotLoginedException;
import com.test.project.exception.user.WrongEmailOrNameException;
import com.test.project.exception.user.WrongPasswordException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(DuplicatedEmailException.class)
    public final ResponseEntity<String> handleDuplicatedEmailException(
        DuplicatedEmailException exception) {
        log.debug("중복된 이메일입니다", exception);
        return DUPLICATED_EMAIL;
    }

    @ExceptionHandler(DuplicatedNicknameException.class)
    public final ResponseEntity<String> handleDuplicatedNicknameException(
        DuplicatedNicknameException exception) {
        log.debug("중복된 닉네임입니다.", exception);
        return DUPLICATED_NICKNAME;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<String> handleUserNotFoundException(
        UserNotFoundException exception) {
        return USER_NOT_FOUND;
    }

    @ExceptionHandler(WrongPasswordException.class)
    public final ResponseEntity<String> handleWrongPasswordException(
        WrongPasswordException exception) {
        log.debug("잘못된 비밀번호입니다.", exception);
        return WRONG_PASSWORD;
    }

    @ExceptionHandler(AlreadyMyPasswordException.class)
    public final ResponseEntity<String> handleAlreadyMyPasswordException(
        AlreadyMyPasswordException exception) {
        log.debug("이미 내가 사용중인 비밀번호입니다.", exception);
        return ALREADY_MY_PASSWORD;
    }

    @ExceptionHandler(WrongEmailOrNameException.class)
    public final ResponseEntity<String> handleWrongEmailOrNameException(
        WrongEmailOrNameException exception) {
        log.debug("이메일 혹은 이름을 잘못 입력했습니다.", exception);
        return WRONG_EMAIL_OR_NAME;
    }

    @ExceptionHandler(UserNotLoginedException.class)
    public final ResponseEntity<String> handleUserNotLoginedException() {
        return NOT_LOGINED_USER;
    }

}

