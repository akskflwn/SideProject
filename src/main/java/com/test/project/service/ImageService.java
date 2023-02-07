package com.test.project.service;

import static com.test.project.constants.DefaultImageConstants.DEFAULT_PROFILE_IMAGE_ID;

import com.test.project.dto.ImageDto;
import com.test.project.entity.Board;
import com.test.project.entity.Image;
import com.test.project.entity.User;
import com.test.project.exception.image.ImageNotFoundException;
import com.test.project.exception.user.UserNotMatchException;
import com.test.project.repository.ImageRepository;
import com.test.project.util.aws.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final AwsS3Service awsS3Service;

    private final int SPLIT_INDEX = 57;

    public Image getImageByImageUrl(String imageUrl) {
        return imageRepository.findByImageUrl(imageUrl)
            .orElseThrow(ImageNotFoundException::new);
    }

    public Image getImageById(Long imageId) {
        return imageRepository.findById(imageId)
            .orElseThrow(ImageNotFoundException::new);
    }

    @Transactional
    public Image updateProfileImage(MultipartFile profileImage) {
        String imageUrl = awsS3Service.uploadImage(profileImage);
        Image image = imageRepository.save(ImageDto.of(imageUrl));

        return image;
    }

    //프로필 이미지가 기본 이미지가 아닐경우에만 이미지 테이블 삭제
    @Transactional
    public void deleteProfileImage(Image image) {
        if (image.getId() != DEFAULT_PROFILE_IMAGE_ID) {
            deleteImage(image);
        }
    }




    public Image savePostImage(MultipartFile postImage) {
        String imageUrl = awsS3Service.uploadImage(postImage);
        Image image = Image.builder()
            .imageUrl(imageUrl)
            .build();
        return imageRepository.save(image);
    }





    @Transactional
    public void deleteImage(Image targetImage) {
        String fileName = targetImage.getImageUrl().substring(SPLIT_INDEX);
        awsS3Service.deleteImage(fileName);
        imageRepository.delete(targetImage);
    }

}
