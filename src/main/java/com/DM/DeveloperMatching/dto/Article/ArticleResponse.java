package com.DM.DeveloperMatching.dto.Article;

import com.DM.DeveloperMatching.domain.Article;
import com.DM.DeveloperMatching.domain.Level;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ArticleResponse {
    private Long aId;
    private Long articleOwnerId;
    private String title;
    private Integer maximumMember;
    private List<String> recPart;
    private List<String> recTech;
    private Level recLevel;
    private String during;
    private Date due;
    private String content;
    private String projectImg;

    public ArticleResponse(Article article, String projectImg) {
        this.aId = article.getAId();
        this.articleOwnerId = article.getArticleOwner().getUId();
        this.title = article.getTitle();
        this.maximumMember = article.getMaximumMember();
        this.recPart = Arrays.asList(article.getRecPart().split(", \\s*"));
        this.recTech = Arrays.asList(article.getRecTech().split(", \\s*"));
        this.recLevel = article.getRecLevel();
        this.during = article.getDuring();
        this.due = article.getDue();
        this.content = article.getContent();
        this.projectImg = projectImg;
    }
}