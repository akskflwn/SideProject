package com.test.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @JsonIgnore
    @OneToMany(mappedBy = "image")
    private List<User> users = new ArrayList<>();

    @Builder
    public Image(Long id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }
}