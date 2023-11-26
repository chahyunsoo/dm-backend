package com.DM.DeveloperMatching.dto.User;

import com.DM.DeveloperMatching.domain.Career;
import com.DM.DeveloperMatching.domain.History;
import com.DM.DeveloperMatching.domain.Level;
import com.DM.DeveloperMatching.domain.User;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResumeResponse {
    private Long uId;
    private String userName;
    private String part;
    private Level level;
    private String introduction;
    private List<String> tech;
    private List<Career> careerList;
    private List<History> history;
    private String userImg;

    public ResumeResponse(User user, String userImg) {
        this.uId = user.getUId();
        this.userName = user.getUserName();
        this.part = user.getPart();
        this.level = user.getLevel();
        this.introduction = user.getIntroduction();
        this.tech = Arrays.asList(user.getTech().split(", \\s*"));
        this.careerList = user.getCareerList();
        this.history = user.getHistory();
        this.userImg = userImg;
    }
}
