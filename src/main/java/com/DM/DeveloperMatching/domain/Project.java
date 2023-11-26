package com.DM.DeveloperMatching.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long pId;

    @Column(name = "member_count")
    private int memberCnt;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_status")
    private ProjectStatus projectStatus;

    @Column(name = "likes")
    private Integer likes;

    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL) //양방향 잡을라고
    private List<Member> projectInMember = new ArrayList<>();

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL)    //Json 무한 루프 문제 발생
    private Article article;                                      //@JsonBackReference를 사용하면 Project에서
                                                                    //article 접근도 안됨

    public void updateProjectStatus(ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }

    public void increaseLikes() {
        this.likes++;
    }

    public void decreaseLikes() {
        this.likes--;
    }

    @Builder
    public Project(int memberCnt, ProjectStatus projectStatus, int likes) {
        this.memberCnt = memberCnt;
        this.projectStatus = projectStatus;
        this.likes = likes;
    }


}
