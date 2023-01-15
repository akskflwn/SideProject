package com.test.project.entity.user;

import static com.test.project.constants.ResponseConstants.CREATED;

import com.test.project.entity.user.UserDto.CreateRequest;
import com.test.project.entity.user.UserDto.LoginRequest;
import com.test.project.entity.user.UserDto.MyInfoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
@RequestMapping("v1")
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Void> createUser(@Valid @RequestBody CreateRequest requestDto) {
        userService.create(requestDto);
        return CREATED;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest loginRequestDto){
        String token = userService.login(loginRequestDto);
        ResponseCookie responseCookie = ResponseCookie.from("access-token",token)
            .httpOnly(true)
            .path("/")
            .maxAge(12*(60*60) + 9 * (60 * 60))
            .build();
        log.info("발급 토큰 = {}",token);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).build();
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from("access-token", null)
            .httpOnly(true)
            .path("/")
            .maxAge(0)
            .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    @GetMapping("/mypage")
    public ResponseEntity<MyInfoResponse> getLoginInformation(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(userService.getMyPageInfo(userId));
    }
}
