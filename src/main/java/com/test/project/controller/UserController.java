package com.test.project.controller;

import static com.test.project.constants.ResponseConstants.CREATED;
import static com.test.project.constants.ResponseConstants.OK;

import com.test.project.dto.BoardDto.MyBoardResponse;
import com.test.project.dto.UserDto.CreateRequest;
import com.test.project.dto.UserDto.DeleteRequest;
import com.test.project.dto.UserDto.LoginRequest;
import com.test.project.dto.UserDto.MyInfoResponse;
import com.test.project.dto.UserDto.UpdateRequest;
import com.test.project.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class UserController {

    private final UserService userService;

    /**
     * 회웝가입 메서드
     *
     * @param requestDto
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<Void> createUser(@Valid @RequestBody CreateRequest requestDto) {
        log.info("컨트롤러 실행");
        userService.create(requestDto);
        return CREATED;
    }

    /**
     * 로그인 메서드
     *
     * @param requestDto
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest requestDto) {
        String token = userService.login(requestDto);
        ResponseCookie responseCookie = ResponseCookie.from("access-token", token)
            .httpOnly(true)
            .path("/")
            .maxAge(12 * (60 * 60) + 9 * (60 * 60))
            .build();
        log.info("발급 토큰 = {}", token);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString())
            .build();
    }

    /**
     * 로그아웃 메서드
     *
     * @return 쿠기 생명주기 0으로 변경
     */
    @GetMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from("access-token", null)
            .httpOnly(true)
            .path("/")
            .maxAge(0)

            .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    /**
     * 내정보 페이지
     *
     * @param userId
     * @return userDto(MyInfoResponse)
     */
    @GetMapping("/mypage")
    public ResponseEntity<MyInfoResponse> getLoginInformation(
        @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(userService.getMyPageInfo(userId));
    }

    /**
     * 회원 정보 수정 메서드
     *
     * @param requestDto
     * @param userId
     * @return ResponseEntity.ok().build();
     */
    @PutMapping("/update")
    public ResponseEntity<Void> update(@Valid @RequestBody UpdateRequest requestDto,
        @AuthenticationPrincipal Long userId) {
        userService.update(requestDto, userId);

        return OK;
    }

    /**
     * 회원 탈퇴 메서드
     *
     * @param requestDto
     * @param userId
     * @return ResponseEntity.ok().build();
     */
    @PostMapping("/delete")
    public ResponseEntity<Void> delete(@Valid @RequestBody DeleteRequest requestDto,
        @AuthenticationPrincipal Long userId) {
        userService.delete(requestDto, userId);

        return OK;
    }

    /**
     * 내가 작성한 게시글 조회 메서드
     * @param pageable
     * @param userId
     * @return
     */
    @GetMapping("mypage/board")
    public ResponseEntity<Page> getMyBoards(@PageableDefault Pageable pageable, @AuthenticationPrincipal Long userId){
        return ResponseEntity.ok(userService.getMyBoards(userId,pageable));
    }

    /**
     * 내가 좋아요 누른 게시글 조회 메서드
     *
     * @param pageable
     * @param userId
     * @return Page
     */
    @GetMapping("/mypage/board/liked")
    public ResponseEntity<Page<MyBoardResponse>> getBoardILiked(@PageableDefault Pageable pageable,
        @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(userService.getBoardsILiked(userId, pageable));
    }

    /**
     * 내가 댓글 작성한 게시글 조회 메서드
     *
     * @param pageable
     * @param userId
     * @return Page
     */
    @GetMapping("/mypage/board/replied")
    public ResponseEntity<Page<MyBoardResponse>> getBoardsIReplied(
        @PageableDefault Pageable pageable,
        @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(userService.getBoardsIReplied(userId, pageable));
    }

}
