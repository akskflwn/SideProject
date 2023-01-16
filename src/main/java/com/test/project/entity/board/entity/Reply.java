package com.test.project.entity.board.entity;


import static jakarta.persistence.FetchType.LAZY;

import com.test.project.entity.BaseTimeEntity;
import com.test.project.entity.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_reply_id")
    private Reply parentReply;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "to_user_id")
    private User parentUser;

    @OneToMany(mappedBy = "parentReply")
    @OrderBy("createdAt asc")
    private List<Reply> children = new ArrayList<>();

    private String content;

    private boolean isDeleted;

    public void setParentUser(User parentUser) {
        this.parentUser = parentUser;
    }

    public void setParentReply(Reply parentReply) {
        this.parentReply = parentReply;
    }

    public static Reply createReply(Board board, User user, String content) {
        return Reply.builder()
            .board(board)
            .user(user)
            .content(content).build();
    }

    public void updateReply(String content) {
        this.content = content;
    }

    public void changeDeleteStatus() {
        this.isDeleted = true;
    }


}
