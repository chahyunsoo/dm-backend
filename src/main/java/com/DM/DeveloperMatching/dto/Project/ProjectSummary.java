package com.DM.DeveloperMatching.dto.Project;

import com.DM.DeveloperMatching.dto.Article.ArticleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProjectSummary {
    private String title;
    private String content;
    private String part;
    private String tech;

    public ProjectSummary(ArticleResponse article) {
        this.title = article.getTitle();
        this.content = article.getContent();
        this.part = String.join(", ", article.getRecPart());
        this.tech = String.join(", ", article.getRecTech());
    }
}
