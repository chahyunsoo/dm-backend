package com.DM.DeveloperMatching.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long rId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User reviewUser;

    @Column(name = "review_content")
    private String reviewContent;

    @Builder
    public Review(User reviewUser, String reviewContent) {
        this.reviewUser = reviewUser;
        this.reviewContent = reviewContent;
    }

}
