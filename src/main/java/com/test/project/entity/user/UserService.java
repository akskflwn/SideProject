package com.test.project.entity.user;

import com.test.project.entity.user.UserDto.CreateRequest;
import com.test.project.exception.user.DuplicatedEmailException;
import com.test.project.exception.user.DuplicatedNicknameException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
}
