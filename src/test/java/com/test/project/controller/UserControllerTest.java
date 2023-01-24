package com.test.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.project.dto.UserDto.CreateRequest;
import com.test.project.dto.UserDto.DeleteRequest;
import com.test.project.dto.UserDto.LoginRequest;
import com.test.project.dto.UserDto.MyInfoResponse;
import com.test.project.dto.UserDto.UpdateRequest;
import com.test.project.service.UserService;
import com.test.project.exception.user.AlreadyMyPasswordException;
import com.test.project.exception.user.DuplicatedEmailException;
import com.test.project.exception.user.DuplicatedNicknameException;
import com.test.project.exception.user.WrongPasswordException;
import com.test.project.security.TokenProvider;
import com.test.project.util.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TokenProvider tokenProvider;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    SecurityContext securityContext;

    CreateRequest createRequest;

    @BeforeEach
    public void setup() throws Exception {
        createRequest = CreateRequest.builder()
            .email("akskflwn@gmail.com")
            .password("1234")
            .name("심규영")
            .nickname("akskflwn")
            .build();
    }


    @Test
    @DisplayName("회원가입 - 모든 유효성 검사 통과하면 가입 완료")
    void createUser_Success() throws Exception {
        //given:Mock 객체가 특정 상황에서 해야하는 행위를 정의하는 메소드
        doNothing().when(userService).create(any(CreateRequest.class));

        //andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(
                post("/v1/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andDo(print())
            .andExpect(status().isCreated());

        verify(userService).create(any(CreateRequest.class));
    }

    @Test
    @DisplayName("회원가입 - 중복된 이메일로 가입실패")
    void createUser_Failure_Duplicated_Email() throws Exception {
        doThrow(new DuplicatedEmailException()).when(userService).create(any(CreateRequest.class));

        mockMvc.perform(
                post("/v1/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andDo(print())
            .andExpect(status().isConflict());

        verify(userService).create(any(CreateRequest.class));
    }

    @Test
    @DisplayName("회원가입 - 중복된 닉네임으로 가입실패")
    void createUser_Failure_Duplicated_Nickname() throws Exception {
        doThrow(new DuplicatedNicknameException()).when(userService).create(any(CreateRequest.class));

        mockMvc.perform(
                post("/v1/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andDo(print())
            .andExpect(status().isConflict());

        verify(userService).create(any(CreateRequest.class));
    }


    @Test
    @DisplayName("로그인 - 유저 토큰 반환")
    void login_Success() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
            .email("akskflwn@gmail.com")
            .password("1234")
            .build();

        given(userService.login(any(LoginRequest.class)))
            .willReturn("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjcwMzMwMDYxLCJleHAiOjE2NzAzNzMyNjF9.Knct2hDifWGO2c7ZSOSI79zKzIt8_eB3wnW7BiwmuJI");

        mockMvc.perform(
                post("/v1/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(loginRequest)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(cookie().value("access-token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjcwMzMwMDYxLCJleHAiOjE2NzAzNzMyNjF9.Knct2hDifWGO2c7ZSOSI79zKzIt8_eB3wnW7BiwmuJI"));
    }

    @Test
    @DisplayName("마이페이지 - 로그인 한 회원정보 반환 성공")
    @WithMockCustomUser
    void getMyPageInformation_Success() throws Exception {
        given(userService.getMyPageInfo(1L))
            .willReturn(MyInfoResponse.builder()
                .email("akskflwn@gmail.com")
                .nickname("akskflwn")
                .name("심규영")
                .build()
            );

        mockMvc.perform(
                get("/v1/mypage")
                    .characterEncoding("utf-8"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nickname").value("akskflwn"))
            .andExpect(jsonPath("$.email").value("akskflwn@gmail.com"))
            .andExpect(jsonPath("$.name").value("심규영"));
    }

    @Test
    @DisplayName("회원정보 수정 - 비밀번호 수정 성공")
    @WithMockCustomUser
    void updatePassword_Success() throws Exception {
        UpdateRequest request = UpdateRequest.builder()
            .nickname("마나리주")
            .currentPassword("4321")
            .newPassword("1234")
            .build();

        doNothing().when(userService).update(request, 1L);

        mockMvc.perform(
                put("/v1/update")
                    .characterEncoding("UTF-8")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk());

        verify(userService).update(any(UpdateRequest.class), any(Long.class));
    }

    @Test
    @DisplayName("회원정보 수정 - 기존 비밀번호가 일치하지 않아 비밀번호 수정 실패")
    @WithMockCustomUser
    void updatePassword_Failure_WrongPassword() throws Exception {
        UpdateRequest request = UpdateRequest.builder()
            .nickname("updated")
            .currentPassword("1234")
            .newPassword("5678")
            .build();

        doThrow(new WrongPasswordException()).when(userService).update(any(UpdateRequest.class), any(Long.class));

        mockMvc.perform(
                put("/v1/update")
                    .characterEncoding("UTF-8")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isUnauthorized());

        verify(userService).update(any(UpdateRequest.class), any(Long.class));
    }

    @Test
    @DisplayName("회원정보 수정 - 새로운 비밀번호가 이미 기존 비밀번호와 동일할 때 수정 실패")
    @WithMockCustomUser
    void updatePassword_Failure_AlreadyMyPassword() throws Exception {
        UpdateRequest request = UpdateRequest.builder()
            .nickname("구영")
            .currentPassword("1234qwer!")
            .newPassword("1234qwer!")
            .build();

        doThrow(new AlreadyMyPasswordException()).when(userService).update(any(UpdateRequest.class), any(Long.class));

        mockMvc.perform(
                put("/v1/update")
                    .characterEncoding("UTF-8")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isConflict());

        verify(userService).update(any(UpdateRequest.class), any(Long.class));
    }

    @Test
    @DisplayName("회원탈퇴 - 성공")
    @WithMockCustomUser
    void delete_Success() throws Exception {
        DeleteRequest requestDto = DeleteRequest.builder()
            .password("password")
            .build();

        doNothing().when(userService).delete(any(DeleteRequest.class), anyLong());

        mockMvc.perform(
                post("/v1/delete")
                    .characterEncoding("UTF-8")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
            .andDo(print())
            .andExpect(status().isOk());

        verify(userService).delete(any(DeleteRequest.class), anyLong());
    }

    @Test
    @DisplayName("회원탈퇴 - 잘못된 비밀번호로 실패")
    @WithMockCustomUser
    void delete_Failure_WrongPassword() throws Exception {
        DeleteRequest requestDto = DeleteRequest.builder()
            .password("1234")
            .build();

        doThrow(new WrongPasswordException()).when(userService).delete(any(DeleteRequest.class), anyLong());

        mockMvc.perform(
                post("/v1/delete")
                    .characterEncoding("UTF-8")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
            .andDo(print())
            .andExpect(status().isUnauthorized());

        verify(userService).delete(any(DeleteRequest.class), anyLong());
    }

    @Test
    @DisplayName("마이페이지 - 내가 좋아요 한 게시물 리스트")
    @WithMockCustomUser
    void getPostsILiked_Success() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page response = new PageImpl(new ArrayList());

        given(userService.getBoardsILiked(1L, pageable))
            .willReturn(response);

        mockMvc.perform(
                get("/v1/mypage/board/liked")
                    .characterEncoding("UTF-8")
                    .param("size", "10")
                    .param("page", "0"))
            .andDo(print())
            .andExpect(status().isOk());

        verify(userService).getBoardsILiked(anyLong(), any(Pageable.class));

    }

    @Test
    @DisplayName("마이페이지 - 내가 댓글 단 게시물 리스트")
    @WithMockCustomUser
    void getPostsIReplied_Success() throws Exception {

        Pageable pageable = PageRequest.of(0, 10);
        Page response = new PageImpl(new ArrayList());

        given(userService.getBoardsIReplied(1L, pageable))
            .willReturn(response);

        mockMvc.perform(
                get("/v1/mypage/board/replied")
                    .characterEncoding("UTF-8")
                    .param("size", "10")
                    .param("page", "0"))
            .andDo(print())
            .andExpect(status().isOk());

        verify(userService).getBoardsIReplied(anyLong(), any(Pageable.class));

    }
}