package com.test.project.entity.board.controller;

import static com.test.project.constants.SortStatus.DEFAULT;
import static com.test.project.constants.SortStatus.LIKES;

import com.test.project.entity.board.dto.BoardDto;
import com.test.project.entity.board.dto.BoardDto.UpdateRequest;
import com.test.project.entity.board.dto.LikeDto;
import com.test.project.entity.board.dto.ReplyDto.Request;
import com.test.project.entity.board.dto.ReplyDto.SuperRequest;
import com.test.project.entity.board.service.BoardService;
import com.test.project.exception.user.UserNotLoginedException;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.test.project.entity.board.dto.BoardDto.SaveRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/boards")
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시물 생성 메서드
     *
     * @param savedRequestDto
     * @param userId
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<Long> saveBoard(@Valid @RequestBody SaveRequest savedRequestDto,
        @AuthenticationPrincipal Long userId) {
        if (userId == null) {
            throw new UserNotLoginedException();
        }
        return ResponseEntity.ok(boardService.saveBoard(savedRequestDto, userId));
    }

    /**
     * 특정 게시물 수정
     *
     * @param boardId
     * @param updateRequestDto
     * @param userId
     * @return
     */
    @PostMapping("/update/{boardId}")
    public ResponseEntity<Long> updateBoard(@PathVariable Long boardId, @Valid @RequestBody
    UpdateRequest updateRequestDto, @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(boardService.updateBoard(boardId, updateRequestDto, userId));
    }

    /**
     * 특정 게시글 조회 메서드
     *
     * @param boardId
     * @param userId
     * @return
     */
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDto.Response> getBoard(@PathVariable Long boardId,
        @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(boardService.getBoardDetail(boardId, userId));
    }

    /**
     * 게시글 전체 조회 (id 기준 내림차순으로 출력한다.)
     *
     * @param userId
     * @param pageable
     * @return Page
     */
    @GetMapping("/list")
    public ResponseEntity<Page<BoardDto.Response>> getBoardList(
        @AuthenticationPrincipal Long userId, @PageableDefault
    Pageable pageable) {
        return ResponseEntity.ok(boardService.getBoardList(userId, pageable, DEFAULT));
    }

    /**
     * 게시글 전체 조회 (좋아요 순, id 내림차순으로 출력한다.)
     *
     * @param userId
     * @param pageable
     * @return
     */
    @GetMapping("/list/likes")
    public ResponseEntity<Page<BoardDto.Response>> getBoardListByLikes(
        @AuthenticationPrincipal Long userId, @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(boardService.getBoardList(userId, pageable, LIKES));
    }

    /**
     * 게시물 삭제
     *
     * @param boardId
     * @param userId
     * @return HttpStatus.ok
     */
    @PostMapping("/delete/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId,
        @AuthenticationPrincipal Long userId) {
        boardService.deleteBoard(boardId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 부모 댓글 작성
     *
     * @param superRequestDto
     * @param userId
     * @return replyId
     */
    @PostMapping("/reply/create/super")
    public ResponseEntity<Long> saveSuperReply(@Valid @RequestBody SuperRequest superRequestDto,
        @AuthenticationPrincipal Long userId) {
        if (userId == null) {
            throw new UserNotLoginedException();
        }
        return ResponseEntity.ok(boardService.saveSuperReply(userId, superRequestDto));
    }

    /**
     * 자식 댓글 작성
     *
     * @param subRequestDto
     * @param userId
     * @return replyId
     */
    @PostMapping("/reply/create/sub")
    public ResponseEntity<Long> saveSubReply(@Valid @RequestBody Request subRequestDto,
        @AuthenticationPrincipal Long userId) {
        if (userId == null) {
            throw new UserNotLoginedException();
        }
        return ResponseEntity.ok(boardService.saveSubReply(userId, subRequestDto));
    }

    /**
     * 댓글 수정 메서드
     *
     * @param dto
     * @param userId
     * @return replyId
     */
    @PostMapping("/reply/update")
    public ResponseEntity<Long> updateReply(@Valid @RequestBody Request dto,
        @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(boardService.updateReply(dto, userId));
    }

    /**
     * 댓글 삭제 메서드
     *
     * @param replyId
     * @param userId
     * @return void
     */
    @PostMapping("/reply/delete/{replyId}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long replyId,
        @AuthenticationPrincipal Long userId) {
        boardService.deleteReply(replyId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/like/{boardId}")
    public ResponseEntity<LikeDto> hitLike(@PathVariable Long boardId, @AuthenticationPrincipal Long userId) {
        if(userId == null){
            throw new UserNotLoginedException();
        }

        return ResponseEntity.ok(boardService.likeProcess(boardId,userId));
    }
}
