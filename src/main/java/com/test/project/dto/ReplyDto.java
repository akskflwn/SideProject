package com.test.project.dto;

import com.test.project.entity.Reply;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReplyDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Request {

        //댓글 Id
        @NotNull
        private Long replyId;
        //댓글 내용
        @NotBlank
        private String content;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class SuperRequest {

        //게시글 Id
        @NotNull
        private Long boardId;
        //댓글 내용
        @NotBlank
        private String content;
    }

    @Getter
    public static class Response {

        // 댓글 Id
        private Long id;
        // 댓글 작성자 Id
        private Long userId;
        // 댓글 작성자 닉네임
        private String userNickname;
        // 댓글 내용
        private String content;
        // 대댓글 작성자 닉네임
        private String targetNickname;
        // 댓글 삭제 여부
        private Boolean deleteStatus;
        // 댓글 작성 시간
        private LocalDateTime createAt;
        // 댓글 수정 시간
        private LocalDateTime updatedAt;
        // 대댓글 리스트
        private List<Response> children;

        @Builder
        public Response(Reply reply, String targetNickname, List<Response> children) {
            this.id = reply.getId();
            this.userId = reply.getUser().getId();
            this.userNickname = reply.getUser().getNickname();
            this.content = reply.getContent();
            this.createAt = reply.getCreatedAt();
            this.updatedAt = reply.getUpdatedAt();
            this.children = children;
            this.deleteStatus = reply.isDeleted();
            this.targetNickname = targetNickname;

        }


    }

}
