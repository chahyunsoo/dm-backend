package com.DM.DeveloperMatching.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Suggest {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "suggest_id")
    private Long sId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @ManyToOne
    @JoinColumn(name = "article_owner_id")
    private User articleOwner;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project projectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_status")
    private MemberStatus suggestStatus;

    public void update(MemberStatus suggestStatus) {
        this.suggestStatus = suggestStatus;
    }

    @Builder
    public Suggest(User userId, User articleOwner, Project projectId, MemberStatus suggestStatus) {
        this.userId = userId;
        this.articleOwner = articleOwner;
        this.projectId = projectId;
        this.suggestStatus = suggestStatus;
    }

}
