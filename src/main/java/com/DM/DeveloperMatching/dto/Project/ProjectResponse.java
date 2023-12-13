package com.DM.DeveloperMatching.dto.Project;

import com.DM.DeveloperMatching.controller.ArticleController;
import com.DM.DeveloperMatching.domain.Level;
import com.DM.DeveloperMatching.domain.Project;
import com.DM.DeveloperMatching.domain.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProjectResponse {
    private Long pId;
    private Long aId;
    private List<String> recPart;
    private List<String> recTech;
    private Level recLevel;
    private String title;
    private String projectImg;
    private Integer memberCnt;
    private ProjectStatus projectStatus;
    private Integer likes;

    public ProjectResponse(Project project, String projectImg) {
        this.pId = project.getPId();
        this.aId = project.getArticle().getAId();
        this.recPart = Arrays.asList(project.getArticle().getRecPart().split(", \\s*"));
        this.recTech = Arrays.asList(project.getArticle().getRecTech().split(", \\s*"));
        this.recLevel = project.getArticle().getRecLevel();
        this.title = project.getArticle().getTitle();
        this.projectImg = projectImg;
        this.memberCnt = project.getMemberCnt();
        this.projectStatus = project.getProjectStatus();
        this.likes = project.getLikes();
    }
}
