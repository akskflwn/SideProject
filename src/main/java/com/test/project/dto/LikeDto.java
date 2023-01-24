package com.test.project.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeDto {

    private boolean likeStatus;

    private int count;

}
