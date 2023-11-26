package com.DM.DeveloperMatching.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")  //반드시 필요, user_id랑 project_id를 묶어서 P.K로 할 수도 있지만 의미없는 값을 넣는게 좋음 -> 유연성이 생김
    private Long mId;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_status")
    private MemberStatus memberStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    public void update(MemberStatus memberStatus) {
        this.memberStatus = memberStatus;
    }

    @Builder
    public Member(MemberStatus memberStatus, User user, Project project) {
        this.memberStatus = memberStatus;
        this.user = user;
        this.project = project;
    }

}
