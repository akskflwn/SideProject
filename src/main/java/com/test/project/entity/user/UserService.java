package com.test.project.entity.user;

import com.test.project.entity.user.UserDto.CreateRequest;
import com.test.project.entity.user.UserDto.LoginRequest;
import com.test.project.exception.user.DuplicatedEmailException;
import com.test.project.exception.user.DuplicatedNicknameException;
import com.test.project.exception.user.UserNotFoundException;
import com.test.project.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    public void create(CreateRequest requestDto) {
        checkDuplicatedForCreate(requestDto);

        User user = requestDto.toEntity();
        userRepository.save(user);
    }

    @Transactional
    public void checkDuplicatedForCreate(CreateRequest requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicatedEmailException();
        }
        if (userRepository.existsByNickname(requestDto.getNickname())) {
            throw new DuplicatedNicknameException();
        }
    }

    @Transactional
    public String login(LoginRequest loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));

        if (!loginRequestDto.checkPassword(user.getPassword())) {
            throw new UserNotFoundException("이메일 또는 비밀번호가 일치하지 않습니다");
        }
        String token = tokenProvider.create(user);
        return token;
    }
}
