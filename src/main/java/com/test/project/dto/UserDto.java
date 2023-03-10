package com.test.project.dto;

import static com.test.project.constants.RegexConstants.PASSWORD;

import com.test.project.entity.User;
import com.test.project.util.encrption.EncryptionService;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CreateRequest {

        @NotBlank(message = "이메일 주소를 입력해주세요")
        @Email(message = "이메일 형시게 맞게 입력해주세요")
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요")
        @Size(min = 3, max = 12, message = "숫자, 문자, 특수문자 중 2가지를 조합해주세요")
        @Pattern(regexp = PASSWORD, message = "숫자, 문자, 특수문자 중 2가지를 조합해 입력해주세요")
        private String password;

        @NotBlank(message = "닉네임을 입력해주세요")
        @Size(min = 3, max = 12, message = "닉네임은 3자 이상 12자 이하로 작성해주세요, 문자, 특수문자 중 2가지를 조합해주세요")
        private String nickname;

        @NotBlank(message = "이름을 입력해주세요")
        @Size(min = 2, message = "2자 이상 올바른 이름을 입력해주세요")
        private String name;


        //userDTo의 비밀번호를 BCrypt방식으로 암호화
        public void encryptPassword(EncryptionService encryptionService) {
            this.password = encryptionService.encrypt(password);
        }

        public User toEntity() {
            return User.builder()
                .email(this.email)
                .name(this.name)
                .nickname(this.nickname)
                .password(this.password)
                .build();
        }
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LoginRequest {

        @NotBlank(message = "이메일 주소를 입력해주세요")
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요")
        private String password;

        public boolean checkPassword(EncryptionService encryptionService,
            String encryptedPassword) {
            return encryptionService.isSamePassword(this.password, encryptedPassword);
        }
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LoginResponse {

        private String token;

        public void createToken(String token) {
            this.token = token;
        }
    }

    @Getter
    @Builder
    public static class MyInfoResponse {

        private String profileImageUrl;

        private Long id;

        private String email;

        private String name;

        private String nickname;


    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UpdateRequest {

        @NotBlank(message = "닉네임을 입력해주세요")
        @Size(min = 2, max = 12, message = "닉네임은 3자 이상 12자 이하를 입력해주세요")
        private String nickname;

        @NotBlank(message = "기존 비밀번호를 입력해주세요")
        @Size(min = 3, max = 12, message = "숫자, 문자, 특수문자 중 2가지를 조합해주세요")
        @Pattern(regexp = PASSWORD, message = "숫자, 문자, 특수문자 3가지를 조합해 입력해주세요")
        private String currentPassword;

        @NotBlank(message = "비밀번호를 입력해주세요")
        @Size(min = 3, max = 12, message = "숫자, 문자, 특수문자 중 2가지를 조합해주세요")
        @Pattern(regexp = PASSWORD, message = "숫자, 문자, 특수문자 3가지를 조합해 입력해주세요")
        private String newPassword;

        public boolean checkPassword(EncryptionService encryptionService,
            String encryptedPassword) {
            return encryptionService.isSamePassword(this.currentPassword, encryptedPassword);
        }

        public boolean isAlreadyMyPassword() {
            return currentPassword.equals(newPassword);
        }

        public void encryptPassword(EncryptionService encryptionService) {
            this.newPassword = encryptionService.encrypt(newPassword);
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteRequest {

        @NotBlank(message = "비밀번호를 입력해주세요")
        private String password;

        public boolean checkPassword(EncryptionService encryptionService,
            String encryptedPassword) {
            return encryptionService.isSamePassword(this.password, encryptedPassword);
        }
    }
}
