package com.test.project.entity;

import com.test.project.entity.BaseTimeEntity;
import com.test.project.entity.Like;
import com.test.project.dto.UserDto.MyInfoResponse;
import com.test.project.dto.UserDto.UpdateRequest;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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

    @OneToMany(mappedBy = "user")
    private List<Like> postLikeds = new ArrayList<>();


    public MyInfoResponse toUserInfoResponse() {
        return MyInfoResponse.builder()
            .id(this.id)
            .email(this.email)
            .name(this.name)
            .nickname(this.nickname)
            .build();
    }

    public void updateUser(UpdateRequest updateRequestDto) {
        this.nickname = updateRequestDto.getNickname();
        this.password = updateRequestDto.getNewPassword();
    }

    public void updateUser(String newPassword, String nickname) {
        this.nickname = nickname;
        this.password = newPassword;
    }
}
