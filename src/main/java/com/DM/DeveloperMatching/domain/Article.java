package com.DM.DeveloperMatching.domain;

import com.DM.DeveloperMatching.dto.Recommend.RecommendProjectDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Arrays;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long aId;

    @ManyToOne//(fetch = FetchType.LAZY) <= 이거 하면 article 조회할 때 오류남 why? 직렬화 문제 발생 proxy
    @JoinColumn(name = "user_id")
    private User articleOwner;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "maximum_member")
    private Integer maximumMember;

    @Column(name = "recruit_part")
    private String recPart; //list로 해야하지만 List<String> 사용 불가 table이나 class를 하나 더 만들어야할듯

    @Column(name = "recruit_tech")
    private String recTech; //list로 해야하지만 List<String> 사용 불가 table이나 class를 하나 더 만들어야할듯

    @Enumerated(EnumType.STRING)
    @Column(name = "recruit_level")
    private Level recLevel;

    @Column(name = "during")
    private String during;

    @Temporal(TemporalType.DATE)
    private Date due;

    @Column(name = "project_image")
    private String projectImg;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id")
    private Project project;

    public void update(String title, Integer maximumMember, String recPart, String recTech, Level recLevel,
                       String during, Date due, String content, String projectImg) {
        this.title = title;
        this.maximumMember = maximumMember;
        this.recPart = recPart;
        this.recTech = recTech;
        this.recLevel = recLevel;
        this.during = during;
        this.due = due;
        this.content = content;
        this.projectImg = projectImg;
    }

    public void update(String title, Integer maximumMember, String recPart, String recTech, Level recLevel,
                       String during, Date due, String content) {
        this.title = title;
        this.maximumMember = maximumMember;
        this.recPart = recPart;
        this.recTech = recTech;
        this.recLevel = recLevel;
        this.during = during;
        this.due = due;
        this.content = content;
    }

    @Builder
    public Article(User articleOwner, String title, Integer maximumMember, String recPart, String recTech, Level recLevel,
                   String during, Date due, String content, String projectImg) {
        this.articleOwner = articleOwner;
        this.title = title;
        this.maximumMember = maximumMember;
        this.recPart = recPart;
        this.recTech = recTech;
        this.recLevel = recLevel;
        this.during = during;
        this.due = due;
        this.content = content;
        this.projectImg = projectImg;
        this.project = Project.builder() // Article을 생성할 때 자동으로 Project도 생성
                .memberCnt(1) // Project의 memberCnt를 Article의 maximumMember와 일치시킴
                .projectStatus(ProjectStatus.RECRUITING) // 적절한 ProjectStatus 설정
                .build();
    }

    public RecommendProjectDto toDto(Article article, String projectImg) {
        return RecommendProjectDto.builder()
                .aId(article.getAId())
                .pId(article.getProject().getPId())
                .projectImg(projectImg)
                .recLevel(article.getRecLevel())
                .recPart(Arrays.asList(article.getRecPart().split(", \\s*")))
                .recTech(Arrays.asList(article.getRecTech().split(", \\s*")))
                .title(article.getTitle())
                .projectStatus(article.getProject().getProjectStatus())
                .build();
    }
}
