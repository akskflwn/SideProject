package com.test.project.controller;

import static com.test.project.constants.SortStatus.DEFAULT;
import static com.test.project.constants.SortStatus.LIKES;

import com.test.project.dto.BoardDto;
import com.test.project.dto.BoardDto.UpdateRequest;
import com.test.project.dto.LikeDto;
import com.test.project.dto.ReplyDto.Request;
import com.test.project.dto.ReplyDto.SuperRequest;
import com.test.project.service.BoardService;
import com.test.project.exception.user.UserNotLoginedException;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.Update;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import com.test.project.dto.BoardDto.SaveRequest;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/boards")
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시글 생성 메서드
     */
    @PostMapping("/create")
    public ResponseEntity<Long> createBoard(@Valid @RequestPart SaveRequest request,
        @RequestPart(required = false) MultipartFile multipartFile,
        @AuthenticationPrincipal Long userId) {
        if (userId == null) {
            throw new UserNotLoginedException();
        }
        return ResponseEntity.ok(boardService.saveBoard(request, userId,multipartFile));
    }

    /**
     * 게시글 상세 조회 메서드
     */
    @GetMapping("/board/{boardId}")
    public ResponseEntity<BoardDto.Response> getBoard(@PathVariable Long boardId,
        @AuthenticationPrincipal Long currentUserId) {
        return ResponseEntity.ok(boardService.getBoardDetail(boardId, currentUserId));
    }

    /**
     * 게시글 수정 메서드
     */
    @PutMapping("/update/{boardId}")
    public ResponseEntity<Long> updateBoard(@PathVariable Long boardId,
        @Valid @RequestPart UpdateRequest request,
        @RequestPart(required = false) MultipartFile multipartFile,
        @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(boardService.updateBoard(boardId, request, multipartFile, userId));
    }

    /**
     * 게시글 삭제 메서드
     */
    @PostMapping("/delete/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId,
        @AuthenticationPrincipal Long userId) {
        boardService.deleteBoard(boardId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 게시글 전체 조회 (id 기준 내림차순으로 출력한다.)
     */
    @GetMapping("/list/latest")
    public ResponseEntity<Page<BoardDto.Response>> getBoardList(
        @AuthenticationPrincipal Long userId, @PageableDefault
    Pageable pageable) {
        return ResponseEntity.ok(boardService.getBoardList(userId, pageable, DEFAULT));
    }

    /**
     * 게시글 전체 조회 (좋아요 순, id 내림차순으로 출력한다.)
     */
    @GetMapping("/list/likes")
    public ResponseEntity<Page<BoardDto.Response>> getBoardListByLikes(
        @AuthenticationPrincipal Long userId, @PageableDefault Pageable pageable) {
        log.info("likes 실행");
        return ResponseEntity.ok(boardService.getBoardList(userId, pageable, LIKES));
    }


    /**
     * 부모 댓글 작성
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
     */
    @PostMapping("/reply/update")
    public ResponseEntity<Long> updateReply(@Valid @RequestBody Request dto,
        @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(boardService.updateReply(dto, userId));
    }

    /**
     * 댓글 삭제 메서드
     */
    @PostMapping("/reply/delete/{replyId}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long replyId,
        @AuthenticationPrincipal Long userId) {
        boardService.deleteReply(replyId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 좋아요 등록 및 삭제 메서드
     */
    @PostMapping("/like/{boardId}")
    public ResponseEntity<LikeDto> hitLike(@PathVariable Long boardId,
        @AuthenticationPrincipal Long userId) {
        if (userId == null) {
            throw new UserNotLoginedException();
        }

        return ResponseEntity.ok(boardService.likeProcess(boardId, userId));
    }
}
