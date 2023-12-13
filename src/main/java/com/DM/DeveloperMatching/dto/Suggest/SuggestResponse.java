package com.DM.DeveloperMatching.dto.Suggest;

import com.DM.DeveloperMatching.domain.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SuggestResponse {
    private Long sId;
    private Long userId;
    private Long articleOwner;
    private Long pId;
    private List<String> recPart;
    private List<String> recTech;
    private Level recLevel;
    private String title;
    private String projectImg;
    private ProjectStatus projectStatus;
    private Integer likes;
    private MemberStatus suggestStatus;

    public SuggestResponse(Suggest suggest, String projectImg) {
        this.sId = suggest.getSId();
        this.userId = suggest.getUserId().getUId();
        this.articleOwner = suggest.getArticleOwner().getUId();
        this.pId = suggest.getProjectId().getPId();
        this.recPart = Arrays.asList(suggest.getProjectId().getArticle().getRecPart().split(", \\s*"));
        this.recTech = Arrays.asList(suggest.getProjectId().getArticle().getRecTech().split(", \\s*"));
        this.recLevel = suggest.getProjectId().getArticle().getRecLevel();
        this.title = suggest.getProjectId().getArticle().getTitle();
        this.projectImg = projectImg;
        this.projectStatus = suggest.getProjectId().getProjectStatus();
        this.suggestStatus = suggest.getSuggestStatus();
    }
}
