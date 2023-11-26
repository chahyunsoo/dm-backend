package com.DM.DeveloperMatching.dto.Project;

import com.DM.DeveloperMatching.controller.ArticleController;
import com.DM.DeveloperMatching.domain.Project;
import com.DM.DeveloperMatching.domain.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProjectResponse {
    private Long pId;
    private Long aId;
    private String title;
    private String projectImg;
    private Integer memberCnt;
    private ProjectStatus projectStatus;
    private Integer likes;

    public ProjectResponse(Project project, String projectImg) {
        this.pId = project.getPId();
        this.aId = project.getArticle().getAId();
        this.title = project.getArticle().getTitle();
        this.projectImg = projectImg;
        this.memberCnt = project.getMemberCnt();
        this.projectStatus = project.getProjectStatus();
        this.likes = project.getLikes();
    }
}
