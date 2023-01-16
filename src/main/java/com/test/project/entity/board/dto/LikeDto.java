package com.test.project.entity.board.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeDto {

    private boolean likeStatus;

    private int count;

}
