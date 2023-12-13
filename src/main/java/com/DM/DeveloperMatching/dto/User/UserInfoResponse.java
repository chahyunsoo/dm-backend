package com.DM.DeveloperMatching.dto.User;

import com.DM.DeveloperMatching.domain.Career;
import com.DM.DeveloperMatching.domain.Level;
import com.DM.DeveloperMatching.domain.User;
import com.DM.DeveloperMatching.dto.Project.ProjectSummary;
import com.DM.DeveloperMatching.service.ProjectService;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserInfoResponse {
    private String nickName;
    private String userName;
    private String part;
    private Level level;
    private String userImg;
    private Double point;
    private String introduction;
    private List<String> tech;
    private List<Career> career;
    private List<ProjectSummary> projectSummaries;

    public UserInfoResponse(User user, ProjectService projectService, String userImg) {
        this.nickName = user.getNickName();
        this.userName = user.getUserName();
        this.part = user.getPart();
        this.level = user.getLevel();
        this.userImg = userImg;
        this.point = user.getPoint();
        this.introduction = user.getIntroduction();
        this.tech = Arrays.asList(user.getTech().split(", \\s*"));
        this.career = user.getCareerList();
        this.projectSummaries = projectService.extractSummary(user.getUId());
    }
}