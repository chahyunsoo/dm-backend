package com.DM.DeveloperMatching.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Likes {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="likes_id")
    private Long lId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User likesUser;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project likesProject;

    @Builder
    public Likes(User likesUser, Project likesProject) {
        this.likesUser = likesUser;
        this.likesProject = likesProject;
    }

}
