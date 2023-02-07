package com.test.project.dto;
import com.test.project.entity.Image;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class ImageDto {

    @NotBlank(message = "이미지 URL이 비어있습니다")
    private String imageUrl;

    public static Image of(String imageUrl) {
        return Image.builder()
            .imageUrl(imageUrl)
            .build();
    }
}