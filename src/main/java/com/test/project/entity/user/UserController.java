package com.test.project.entity.user;

import static com.test.project.constants.ResponseConstants.CREATED;

import com.test.project.entity.user.UserDto.CreateRequest;
import com.test.project.entity.user.UserDto.LoginRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/user")
    public ResponseEntity<Void> createUser(@Valid @RequestBody CreateRequest requestDto) {
        userService.create(requestDto);
        return CREATED;
    }

//    @PutMapping("/user")
//    public ResponseEntity<Void> update(@Valid @RequestBody UpdateRequest requestDto){
//        userService.update(requestDto);
//        return
//    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest loginRequestDto){
        String token = userService.login(loginRequestDto);
        ResponseCookie responseCookie = ResponseCookie.from("access-token",token)
            .httpOnly(true)
            .path("/")
            .maxAge(12*(60*60) + 9 * (60 * 60))
            .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).build();
    }
}
