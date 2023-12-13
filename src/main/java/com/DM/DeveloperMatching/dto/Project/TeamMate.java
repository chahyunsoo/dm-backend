package com.DM.DeveloperMatching.dto.Project;

import com.DM.DeveloperMatching.domain.Member;
import com.DM.DeveloperMatching.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TeamMate {
    private String nickName;
    private Long uId;
    private String userImg;
    private String introduction;

    public TeamMate(User user, String userImg) {
        this.nickName = user.getNickName();
        this.uId = user.getUId();
        this.userImg = userImg;
        this.introduction = user.getIntroduction();
    }
}
