package com.DM.DeveloperMatching.dto.Suggest;

import com.DM.DeveloperMatching.domain.MemberStatus;
import com.DM.DeveloperMatching.domain.Suggest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SuggestResponse {
    private Long userId;
    private Long articleOwner;
    private Long projectId;
    private String title;
    private String projectImg;
    private MemberStatus suggestStatus;

    public SuggestResponse(Suggest suggest, String projectImg) {
        this.userId = suggest.getUserId().getUId();
        this.articleOwner = suggest.getArticleOwner().getUId();
        this.projectId = suggest.getProjectId().getPId();
        this.title = suggest.getProjectId().getArticle().getTitle();
        this.projectImg = projectImg;
        this.suggestStatus = suggest.getSuggestStatus();
    }
}
