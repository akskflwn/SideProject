package com.test.project.entity;



import static javax.persistence.FetchType.LAZY;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Where(clause = "is_deleted = '0'")
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
