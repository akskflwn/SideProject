package com.test.project.entity.board.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BoardDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyBoardResponse {

        private Long id; //게시물 Id

        private String title; //글 제목

        private String content; //글 내용

        private String nickname; //작성자 닉네임

        private int likeCount; //좋아요 수

        private int view; //조회수

        private boolean isLiked; //내가 누른 좋아요 여부

        private LocalDateTime createdAt;

    }

}
