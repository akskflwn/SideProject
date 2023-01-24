package com.test.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.test.project.dto.BoardDto.MyBoardResponse;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Where(clause = "is_deleted = '0'")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Valid
    @JsonIgnore //@JsonIgnore 데이터를 주고 받을때 해당 데이터는 'ignore' 되어 응답값에 보이지 않게됨
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Size(max = 50)
    private String title;

    private String content;

    private int view;

    private boolean isDeleted;

    @BatchSize(size = 100) //조회할 컬럼 최대 수
    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Like> likes;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Reply> replies;

    public void updateBoard(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void addViewCount(int view) {
        this.view = view + 1;
    }

    public void changeStatus() {
        this.isDeleted = true;
    }

    public MyBoardResponse toMyBoardResponse(User user) {
        return MyBoardResponse.builder()
            .id(this.id)
            .title(this.title)
            .nickname(this.user.getNickname())
            .view(this.view)
            .likeCount(this.likes.size())
            .isLiked(this.getLikes().stream().anyMatch(like -> like.getUser().equals(user)))
            .createdAt(this.getCreatedAt())
            .build();
    }

}
