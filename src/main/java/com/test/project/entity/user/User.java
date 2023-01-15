package com.test.project.entity.user;

import com.test.project.entity.BaseTimeEntity;
import com.test.project.entity.user.UserDto.MyInfoResponse;
import com.test.project.entity.user.UserDto.UpdateRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="USER")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String name;

    private String nickname;

    private String password;


    public MyInfoResponse toUserInfoResponse() {
        return MyInfoResponse.builder()
            .email(this.email)
            .name(this.name)
            .nickname(this.nickname)
            .build();
    }

    public void updateUser(UpdateRequest updateRequestDto) {
        this.nickname = updateRequestDto.getNickname();
        this.password = updateRequestDto.getPassword();
    }
}
