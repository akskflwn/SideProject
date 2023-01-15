package com.test.project.entity.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
        private String password;

        @NotBlank(message = "닉네임을 입력해주세요")
        @Size(min = 3, max = 12, message = "닉네임은 3자 이상 12자 이하로 작성해주세요, 문자, 특수문자 중 2가지를 조합해주세요")
        private String nickname;

        @NotBlank(message = "이름을 입력해주세요")
        @Size(min = 2, message = "2자 이상 올바른 이름을 입력해주세요")
        private String name;

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
    public static class LoginRequest {

        @NotBlank(message = "이메일 주소를 입력해주세요")
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요")
        private String password;

        public boolean checkPassword(String userPassword) {
            return this.password.equals(userPassword);
        }
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LoginResponse{
        private String token;

        public void createToken(String token){
            this.token = token;
        }
    }

    @Getter
    @Builder
    public static class MyInfoResponse {
        private String nickname;
        private String email;
        private String name;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UpdateRequest{

        @NotBlank(message = "닉네임을 입력해주세요")
        @Size(min=2, max = 12 , message = "닉네임은 3자 이상 12자 이하를 입력해주세요")
        private String nickname;
    
        @NotBlank(message = "비밀번호를 입력해주세요")
        @Size(min = 3, max = 12, message = "숫자, 문자, 특수문자 중 2가지를 조합해주세요")
        private String password;
        
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteRequest {
        @NotBlank(message = "비밀번호를 입력해주세요")
        private String password;

        public boolean checkPassword(String userPassword) {
            return this.password.equals(userPassword);
        }
    }
}
