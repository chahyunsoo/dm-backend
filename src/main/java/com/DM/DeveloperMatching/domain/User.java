package com.DM.DeveloperMatching.domain;

import com.DM.DeveloperMatching.dto.Recommend.RecommendUserDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) //기본키 자동으로 1씩 증가함
    @Column(name = "user_id")
    private Long uId;

    @Column(name = "user_name",nullable = false)
    private String userName;

    @Column(name = "nickname", nullable = false)
    private String nickName;

    @Column(name = "email",nullable = false)
    private String email;

    @Column(name = "password",nullable = false)
    private String password;

    @Column(name = "phone_num")
    private String phoneNum;

    @Column(name = "part")
    private String part;

    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    private Level level;

    @Column(name = "user_img")
    private String userImg;

    @Column(name = "point")
    private Double point; //double은 null타입을 가질 수 없으니까 double로..

    @Column(name = "introduction", length = 100)
    private String introduction;

    @Column(name = "tech")
    private String tech;

    @Column(name = "career")
    private String career;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)   //양방향 잡을라고
    private List<Member> userInMember = new ArrayList<>();

    @OneToMany(mappedBy = "likesUser", cascade = CascadeType.ALL)
    private List<Likes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Career> careerList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<History> history = new ArrayList<>();

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL)
    private List<Suggest> suggests = new ArrayList<>();

    public void updateResume(String part, Level level, String introduction, String tech, List<Career> careerList,
                             List<History> history, String userImg) {
        this.part = part;
        this.level = level;
        this.introduction = introduction;
        this.tech = tech;
        this.careerList.addAll(careerList);
        this.history.addAll(history);
        this.userImg = userImg;
    }

    public void updateResume(String part, Level level, String introduction, String tech, List<Career> careerList,
                             List<History> history) {
        this.part = part;
        this.level = level;
        this.introduction = introduction;
        this.tech = tech;
        this.careerList.addAll(careerList);
        this.history.addAll(history);
    }

    public void deleteCareer(String content) {
        Career delete = new Career();
        for(Career c : this.careerList) {
            if(c.getContent().equals(content)) {
                delete = c;
            }
        }
        this.careerList.remove(delete);
    }

    public void deleteHistory(String title) {
        History project = new History();
        for(History p : this.history) {
            if(p.getTitle().equals(title)) {
                project = p;
            }
        }
        this.history.remove(project);
    }

    @Builder
    public User(String userName, String nickName,String email, String password, String phoneNum, String part,
                Level level, Double point, String userImg, String introduction, String tech,
                List<Career> careerList, List<History> history) {
        this.userName = userName;
        this.nickName = nickName;
        this.email = email;
        this.password = password;
        this.phoneNum = phoneNum;
        this.part = part;
        this.level = level;
        this.userImg = userImg;
        this.point = point;
        this.introduction = introduction;
        this.tech = tech;
        this.careerList = careerList;
        this.history = history;
    }

    public RecommendUserDto toDto(User user, String userImg) {
        return RecommendUserDto.builder()
                .uId(user.getUId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .part(user.getPart())
                .tech(Arrays.asList(user.getTech().split(", \\s*")))
                .level(user.getLevel())
                .point(user.getPoint())
                .introduction(user.getIntroduction())
                .userImg(userImg)
                .build();
    }
}
