package com.test.project.entity.board.controller;

import com.test.project.entity.board.dto.BoardDto;
import com.test.project.entity.board.dto.BoardDto.UpdateRequest;
import com.test.project.entity.board.service.BoardService;
import com.test.project.exception.user.UserNotFoundException;
import com.test.project.exception.user.UserNotLoginedException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
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

    @PostMapping("/create")
    public ResponseEntity<Long> saveBoard(@Valid @RequestBody SaveRequest savedRequestDto,
        @AuthenticationPrincipal Long userId) {
        if (userId == null) {
            throw new UserNotLoginedException();
        }
        return ResponseEntity.ok(boardService.saveBoard(savedRequestDto, userId));
    }

    @PostMapping("/update/{boardId}")
    public ResponseEntity<Long> updateBoard(@PathVariable Long boardId, @Valid @RequestBody
    UpdateRequest updateRequestDto, @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(boardService.updateBoard(boardId, updateRequestDto, userId));
    }

    //특정 게시글 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDto.Response> getBoard(@PathVariable Long boardId,
        @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(boardService.getBoardDetail(boardId, userId));
    }

    @PostMapping("/delete/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId,
        @AuthenticationPrincipal Long userId) {
        boardService.deleteBoard(boardId, userId);
        return ResponseEntity.ok().build();
    }
}
