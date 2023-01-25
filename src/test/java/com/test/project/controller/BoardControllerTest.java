package com.test.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.project.constants.SortStatus;
import com.test.project.controller.BoardController;
import com.test.project.dto.BoardDto;
import com.test.project.dto.LikeDto;
import com.test.project.dto.ReplyDto;
import com.test.project.entity.Board;
import com.test.project.entity.User;
import com.test.project.exception.user.UserNotLoginedException;
import com.test.project.security.TokenProvider;
import com.test.project.service.BoardService;
import com.test.project.util.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BoardController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc(addFilters = false)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    BoardService boardService;

    @MockBean
    TokenProvider tokenProvider;

    User testUser;

    Board testBoard;

    @BeforeEach
    void beforeEach() {
        testUser = User.builder()
            .id(1L)
            .name("심규영")
            .nickname("akskflwn")
            .email("akskflwn@gmail.com")
            .password("1234")
            .build();

        testBoard = Board.builder()
            .id(1L)
            .user(testUser)
            .title("제목입니다.")
            .content("내용입니다.")
            .isDeleted(false)
            .likes(Collections.emptyList())
            .build();
    }

    @Nested
    @DisplayName("커뮤니티 게시글 조회 기능")
    class selectBoardTest {

        @Test
        @WithMockCustomUser
        @DisplayName("게시글 전체 조회")
        void selectList() throws Exception {

            Pageable pageable = PageRequest.of(0, 10);

            Page<BoardDto.Response> boards = new PageImpl<>(Collections.emptyList());

            given(boardService.getBoardList(testUser.getId(), pageable, SortStatus.DEFAULT))
                .willReturn(boards);

            mockMvc.perform(
                    get("/v1/boards/list"))
                .andDo(print())
                .andExpect(status().isOk());

            verify(boardService).getBoardList(anyLong(), any(Pageable.class),
                eq(SortStatus.DEFAULT));
        }

        @Test
        @WithMockCustomUser
        @DisplayName("게시글 전체 조회(좋아요 순)")
        void selectPostsLikeDescList() throws Exception {

            Pageable pageable = PageRequest.of(0, 10);

            Page<BoardDto.Response> posts = new PageImpl<>(Collections.emptyList());

            given(boardService.getBoardList(testUser.getId(), pageable,
                SortStatus.LIKES))
                .willReturn(posts);

            mockMvc.perform(
                    get("/v1/boards/list/likes"))
                .andDo(print())
                .andExpect(status().isOk());

            verify(boardService).getBoardList(anyLong(), any(Pageable.class),
                eq(SortStatus.LIKES));
        }

        @Test
        @WithMockCustomUser
        @DisplayName("게시글 상세 조회")
        void selectBoardDetails() throws Exception {

            given(boardService.getBoardDetail(testBoard.getId(), testUser.getId()))
                .willReturn(getResponse());

            mockMvc.perform(
                    get("/v1/boards/1"))
                .andDo(print())
                .andExpect(status().isOk());

            verify(boardService).getBoardDetail(anyLong(), anyLong());
        }

        @Nested
        @DisplayName("게시글 등록 기능")
        class saveBoardTest {

            @Test
            @WithMockCustomUser
            @DisplayName("[성공] 로그인 한 유저가 필수 데이터로 요청했을 때")
            void saveBoard_Success() throws Exception {

                given(boardService.saveBoard(getSaveRequest(), testUser.getId()))
                    .willReturn(1L);

                mockMvc.perform(
                        post("/v1/boards/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(objectMapper.writeValueAsString(getSaveRequest())))
                    .andDo(print())
                    .andExpect(status().isOk());

                verify(boardService).saveBoard(any(BoardDto.SaveRequest.class), anyLong());
            }
        }

        @Test
        @DisplayName("[실패] 로그인을 하지 않은 유저일 때")
        void saveBoardWithoutLogin_Fail() throws Exception {

            given(boardService.saveBoard(getSaveRequest(), testUser.getId()))
                .willThrow(UserNotLoginedException.class);

            mockMvc.perform(
                    post("/v1/boards/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(getSaveRequest())))
                .andDo(print())
                .andExpect(status().is4xxClientError());

            verify(boardService, never()).saveBoard(any(BoardDto.SaveRequest.class), anyLong());
        }

        @Test
        @WithMockCustomUser
        @DisplayName("[실패] 필수 데이터가 없을 때")
        void saveBoardWithoutField_Fail() throws Exception {

            BoardDto.SaveRequest dto = BoardDto.SaveRequest.builder()
                .content("내용입니다.").build();

            given(boardService.saveBoard(dto, testUser.getId()))
                .willThrow(NullPointerException.class);

            mockMvc.perform(
                    post("/v1/boards/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

            verify(boardService, never()).saveBoard(any(BoardDto.SaveRequest.class), anyLong());
        }

    }

    @Nested
    @DisplayName("게시글 수정 기능")
    class updatePostTest {

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 로그인 한 유저가 필수 데이터로 요청했을 때")
        void updateBoard_Success() throws Exception {

            given(boardService.updateBoard(testBoard.getId(), getUpdateRequest(), testUser.getId()))
                .willReturn(1L);

            mockMvc.perform(
                    post("/v1/boards/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(getUpdateRequest())))
                .andDo(print())
                .andExpect(status().isOk());

            verify(boardService).updateBoard(anyLong(), any(BoardDto.UpdateRequest.class),
                anyLong());
        }

        @Test
        @WithMockCustomUser
        @DisplayName("[실패] 필수 데이터가 없을 때")
        void updateBoardWithoutLogin_Fail() throws Exception {

            BoardDto.UpdateRequest dto = BoardDto.UpdateRequest.builder()
                .content("내용입니다.").build();

            given(boardService.updateBoard(testBoard.getId(), dto, testUser.getId()))
                .willThrow(NullPointerException.class);

            mockMvc.perform(
                    post("/v1/boards/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

            verify(boardService, never()).updateBoard(anyLong(), any(BoardDto.UpdateRequest.class),
                anyLong());
        }
    }

    @Test
    @WithMockCustomUser
    @DisplayName("[성공]게시글 삭제")
    void deleteBoard() throws Exception {

        willDoNothing().given(boardService).deleteBoard(testBoard.getId(), testUser.getId());

        mockMvc.perform(
                post("/v1/boards/delete/1"))
            .andDo(print())
            .andExpect(status().isOk());

        verify(boardService).deleteBoard(anyLong(), anyLong());
    }

    @Nested
    @DisplayName("좋아요 기능")
    class likeTest {

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 로그인 한 유저가 요청했을 때")
        void hitLike_Success() throws Exception {

            given(boardService.likeProcess(testBoard.getId(), testUser.getId()))
                .willReturn(getLikes());

            mockMvc.perform(
                    post("/v1/boards/like/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likeStatus").value(true))
                .andExpect(jsonPath("$.count").value(1));

            verify(boardService).likeProcess(anyLong(), anyLong());
        }

        @Test
        @DisplayName("[실패] 로그인을 하지 않은 유저일 때")
        void hitLikeWithoutLogin_Fail() throws Exception {

            given(boardService.likeProcess(testBoard.getId(), null))
                .willThrow(UserNotLoginedException.class);

            mockMvc.perform(
                    post("/v1/boards/like/1"))
                .andDo(print())
                .andExpect(status().is4xxClientError());

            verify(boardService, never()).likeProcess(anyLong(), anyLong());

        }
    }

    @Nested
    @DisplayName("[Super] 댓글 등록 기능")
    class SuperReplyTest {

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 로그인 한 유저가 필수 데이터로 요청했을 때")
        void saveSuperReply_Success() throws Exception {

            given(boardService.saveSuperReply(testUser.getId(), getSuperRequest()))
                .willReturn(1L);

            mockMvc.perform(
                    post("/v1/boards/reply/create/super")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(getSuperRequest())))
                .andDo(print())
                .andExpect(status().isOk());

            verify(boardService).saveSuperReply(anyLong(), any(ReplyDto.SuperRequest.class));
        }

        @Test
        @DisplayName("[실패] 로그인 하지 않았을 때")
        void saveSuperReplyWithoutLogin_Fail() throws Exception {

            given(boardService.saveSuperReply(null, getSuperRequest()))
                .willThrow(UserNotLoginedException.class);

            mockMvc.perform(
                    post("/v1/boards/reply/create/super")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(getSuperRequest())))
                .andDo(print())
                .andExpect(status().is4xxClientError());

            verify(boardService, never()).saveSuperReply(anyLong(),
                any(ReplyDto.SuperRequest.class));
        }

        @Test
        @WithMockCustomUser
        @DisplayName("[실패] 필수 데이터가 없을 때")
        void saveSuperReplyWithoutField_Fail() throws Exception {

            ReplyDto.SuperRequest dto = ReplyDto.SuperRequest.builder()
                .boardId(1L).content("").build();

            given(boardService.saveSuperReply(testUser.getId(), dto))
                .willThrow(NullPointerException.class);

            mockMvc.perform(
                    post("/v1/boards/reply/create/super")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

            verify(boardService, never()).saveSuperReply(anyLong(),
                any(ReplyDto.SuperRequest.class));
        }
    }

    @Nested
    @DisplayName("[Sub] 댓글 등록 기능")
    class SubReplyTest {

        @Test
        @WithMockCustomUser
        @DisplayName("[성공] 로그인 한 유저가 필수 데이터로 요청했을 때")
        void saveSubReply_Success() throws Exception {

            given(boardService.saveSubReply(testUser.getId(), getReplyRequest()))
                .willReturn(1L);

            mockMvc.perform(
                    post("/v1/boards/reply/create/sub")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(getReplyRequest())))
                .andDo(print())
                .andExpect(status().isOk());

            verify(boardService).saveSubReply(anyLong(), any(ReplyDto.Request.class));
        }

        @Test
        @DisplayName("[실패] 로그인 하지 않았을 때")
        void saveSubReplyWithoutLogin_Fail() throws Exception {

            given(boardService.saveSubReply(null, getReplyRequest()))
                .willThrow(UserNotLoginedException.class);

            mockMvc.perform(
                    post("/v1/boards/reply/create/sub")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(getReplyRequest())))
                .andDo(print())
                .andExpect(status().is4xxClientError());

            verify(boardService, never()).saveSubReply(anyLong(), any(ReplyDto.Request.class));
        }

        @Test
        @WithMockCustomUser
        @DisplayName("[실패] 필수 데이터가 없을 때")
        void saveSubReplyWithoutField_Fail() throws Exception {

            ReplyDto.Request dto = ReplyDto.Request.builder()
                .replyId(1L).content("").build();

            given(boardService.saveSubReply(testUser.getId(), dto))
                .willThrow(NullPointerException.class);

            mockMvc.perform(
                    post("/v1/boards/reply/create/sub")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

            verify(boardService, never()).saveSubReply(anyLong(), any(ReplyDto.Request.class));
        }
    }

    @Test
    @WithMockCustomUser
    @DisplayName("댓글 수정 기능")
    void updateReplyTest() throws Exception {

        given(boardService.updateReply(getReplyRequest(), testUser.getId()))
            .willReturn(1L);

        mockMvc.perform(
                post("/v1/boards/reply/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .content(objectMapper.writeValueAsString(getReplyRequest())))
            .andDo(print())
            .andExpect(status().isOk());

        verify(boardService).updateReply(any(ReplyDto.Request.class), anyLong());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("댓글 삭제 기능")
    void deleteReply() throws Exception {

        willDoNothing().given(boardService).deleteReply(1L, testUser.getId());

        mockMvc.perform(
                post("/v1/boards/reply/delete/1"))
            .andDo(print())
            .andExpect(status().isOk());

        verify(boardService).deleteReply(anyLong(), anyLong());
        }

    private BoardDto.SaveRequest getSaveRequest() {
        return BoardDto.SaveRequest.builder()
            .title("제목입니다.")
            .content("내용입니다.").build();
    }

    private BoardDto.UpdateRequest getUpdateRequest() {
        return BoardDto.UpdateRequest.builder()
            .title("제목입니다.")
            .content("내용입니다.").build();
    }

    private BoardDto.Response getResponse() {
        return BoardDto.Response.builder()
            .board(testBoard)
            .replies(Collections.emptyList())
            .isLiked(true).build();
    }

    private LikeDto getLikes() {
        return LikeDto.builder()
            .likeStatus(true)
            .count(1).build();
    }

    private ReplyDto.SuperRequest getSuperRequest() {
        return ReplyDto.SuperRequest.builder()
            .boardId(testBoard.getId())
            .content("댓글 내용입니다.").build();
    }

    private ReplyDto.Request getReplyRequest() {
        return ReplyDto.Request.builder()
            .replyId(1L)
            .content("답댓글 내용입니다.").build();
    }
}
