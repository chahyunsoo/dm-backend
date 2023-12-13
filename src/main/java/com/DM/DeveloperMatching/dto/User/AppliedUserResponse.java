package com.DM.DeveloperMatching.dto.User;

import com.DM.DeveloperMatching.domain.Level;
import com.DM.DeveloperMatching.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AppliedUserResponse {
    private Long uId;
    private String nickName;
    private String part;
    private Level level;
    private String userImg;
    private String introduction;
    private List<String> tech;

    public AppliedUserResponse(User user, String projectImg) {
        this.uId = user.getUId();
        this.nickName = user.getNickName();
        this.part = user.getPart();
        this.level = user.getLevel();
        this.userImg = projectImg;
        this.introduction = user.getIntroduction();
        this.tech = Arrays.asList(user.getTech().split(", \\s*"));
    }
}