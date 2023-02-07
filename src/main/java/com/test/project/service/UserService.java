package com.test.project.service;

import static com.test.project.constants.DefaultImageConstants.DEFAULT_PROFILE_IMAGE_ID;

import com.test.project.dto.BoardDto;
import com.test.project.entity.Board;
import com.test.project.entity.User;
import com.test.project.exception.image.ImageNotFoundException;
import com.test.project.exception.user.UserDeletedException;
import com.test.project.repository.BoardRepository;
import com.test.project.dto.UserDto.CreateRequest;
import com.test.project.dto.UserDto.DeleteRequest;
import com.test.project.dto.UserDto.LoginRequest;
import com.test.project.dto.UserDto.MyInfoResponse;
import com.test.project.dto.UserDto.UpdateRequest;
import com.test.project.exception.user.AlreadyMyPasswordException;
import com.test.project.exception.user.DuplicatedEmailException;
import com.test.project.exception.user.DuplicatedNicknameException;
import com.test.project.exception.user.UserNotFoundException;
import com.test.project.exception.user.WrongPasswordException;
import com.test.project.repository.CategoryRepository;
import com.test.project.repository.ImageRepository;
import com.test.project.repository.UserRepository;
import com.test.project.security.TokenProvider;
import com.test.project.util.encrption.EncryptionService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final BoardRepository boardRepository;
    private final EncryptionService encryptionService;
    private final ImageService imageService;
    private final ImageRepository imageRepository;

    @Transactional
    public void create(CreateRequest requestDto) {
        checkDuplicatedForCreate(requestDto);
        requestDto.encryptPassword(encryptionService);

        User user = requestDto.toEntity();

        user.setImage(imageRepository.findById(DEFAULT_PROFILE_IMAGE_ID)
            .orElseThrow(() -> new ImageNotFoundException("기본 프로필 이미지를 찾지 못했습니다.")));

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
    public String login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));

//        if (user.getIsDeleted()) {
//            throw new UserDeletedException();
//        }

        if (!loginRequest.checkPassword(encryptionService, user.getPassword())) {
            throw new UserNotFoundException("이메일 또는 비밀번호가 일치하지 않습니다");
        }
        String token = tokenProvider.create(user);

        return token;
    }

    @Transactional(readOnly = true)
    public MyInfoResponse getMyPageInfo(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));
        MyInfoResponse myInfoResponse = user.toUserInfoResponse();
        return myInfoResponse;
    }

    @Transactional
    public void update(UpdateRequest updateRequest, MultipartFile profileImage, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));

        checkDuplicatedForUpdate(user, updateRequest);

        if (!updateRequest.checkPassword(encryptionService, user.getPassword())) {
            throw new WrongPasswordException();
        }
        if (updateRequest.isAlreadyMyPassword()) {
            throw new AlreadyMyPasswordException();
        }
        updateRequest.encryptPassword(encryptionService);
        user.updateUser(updateRequest);

        if (!profileImage.isEmpty()) {
            user.setImage(imageService.updateProfileImage(profileImage));
            imageService.deleteProfileImage(user.getImage());
        }
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public void checkDuplicatedForUpdate(User user, UpdateRequest updateRequestDto) {
        if (!user.getNickname().equals(updateRequestDto.getNickname()) &&
            userRepository.existsByNickname(updateRequestDto.getNickname())) {
            throw new DuplicatedNicknameException();
        }
    }

    @Transactional
    public void delete(DeleteRequest requestDto, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        if (!requestDto.checkPassword(encryptionService, user.getPassword())) {
            throw new WrongPasswordException();
        }

        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public Page<BoardDto.MyBoardResponse> getBoardsILiked(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("존재 하지 않는 사용자입니다."));
        Page<Board> boards = boardRepository.findBoardsILiked(pageable, user);

        return new PageImpl<>(toMyPageResponse(boards, user), pageable, boards.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<BoardDto.MyBoardResponse> getBoardsIReplied(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("존재 하지 않는 사용자입니다."));
        Page<Board> boards = boardRepository.findBoardsIReplied(pageable, user);

        return new PageImpl<>(toMyPageResponse(boards, user), pageable, boards.getTotalElements());
    }
    @Transactional(readOnly = true)
    public List<BoardDto.MyBoardResponse> toMyPageResponse(Page<Board> boards, User user) {
        return boards.stream().map(board -> board.toMyBoardResponse(user))
            .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public Page<BoardDto.MyBoardResponse> getMyBoards(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("존재하지 않는 사용자입니다."));
        Page<Board> boards = boardRepository.findByUser(pageable, user);
        return new PageImpl<>(toMyPageResponse(boards,user), pageable, boards.getTotalElements());
    }
}
