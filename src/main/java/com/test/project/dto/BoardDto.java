package com.test.project.dto;

import com.test.project.entity.Board;
import com.test.project.entity.Category;
import com.test.project.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BoardDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SaveRequest {

        @NotNull(message = "카테고리를 선택해 주세요.")
        private Long categoryId;

        @NotNull(message = "제목을 입력해주세요")
        private String title;

        @NotBlank(message = "내용을 입력해주세요")
        private String content;


        public Board toEntity(User user, Category category) {
            return Board.builder()
                .user(user)
                .category(category)
                .title(title)
                .content(content)
                .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UpdateRequest {

        @NotBlank(message = "제목을 입력해 주세요.")
        private String title;

        @NotBlank(message = "내용을 입력해 주세요.")
        private String content;

    }

    @Getter
    public static class Response {

        private Long id;

        private Long userId;

        private Long categoryId;

        private String userNickname;

        private String userProfileImage;

        private String categoryName;

        private String title;

        private String content;

        private int view;

        private int likeCount;

        private String mainImage;

        private boolean likeStatus;

        private List<ReplyDto.Response> replyList;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;


        @Builder
        public Response(Board board, List<ReplyDto.Response> replies, boolean isLiked) {
            this.id = board.getId();
            this.userId = board.getUser().getId();
            this.categoryId = board.getCategory().getId();
            this.userNickname = board.getUser().getNickname();
            this.userProfileImage = board.getUser().getImage().getImageUrl();
            this.categoryName = board.getCategory().getCategoryName();
            this.title = board.getTitle();
            this.content = board.getContent();
            this.view = board.getView();
            this.likeCount = board.getLikes().size();
            this.mainImage = board.getImage().getImageUrl();
            this.likeStatus = isLiked;
            this.replyList = replies;
            this.createdAt = board.getCreatedAt();
            this.updatedAt = board.getUpdatedAt();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyBoardResponse {

        private Long id; //게시물 Id

        private String categoryName;
        
        private String title; //글 제목

        private String content; //글 내용
    
        private String nickname; //작성자 닉네임
        
        private String userImage; // 작성자 이미지

        private int likeCount; //좋아요 수

        private int view; //조회수

        private String boardImage; // 게시글 이미지

        private boolean isLiked; //내가 누른 좋아요 여부

        private LocalDateTime createdAt;

    }
}
