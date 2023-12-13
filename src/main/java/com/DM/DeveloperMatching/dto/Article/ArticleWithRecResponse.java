package com.DM.DeveloperMatching.dto.Article;

import com.DM.DeveloperMatching.domain.Article;
import com.DM.DeveloperMatching.domain.Level;
import com.DM.DeveloperMatching.dto.Recommend.RecommendUserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter

public class ArticleWithRecResponse {
    private Long aId;
    private Long articleOwnerId;
    private String articleOwnerNickName;
    private String articleOwnerImg;
    private String title;
    private Integer maximumMember;
    private List<String> recPart;
    private List<String> recTech;
    private Level recLevel;
    private String during;
    private Date due;
    private String content;
    private String projectImg;
    private List<RecommendUserDto> recs;

    public ArticleWithRecResponse(Article article, String projectImg, String userImg, List<RecommendUserDto> recs) {
        this.aId = article.getAId();
        this.articleOwnerId = article.getArticleOwner().getUId();
        this.articleOwnerNickName = article.getArticleOwner().getNickName();
        this.articleOwnerImg = userImg;
        this.title = article.getTitle();
        this.maximumMember = article.getMaximumMember();
        this.recPart = Arrays.asList(article.getRecPart().split(", \\s*"));
        this.recTech = Arrays.asList(article.getRecTech().split(", \\s*"));
        this.recLevel = article.getRecLevel();
        this.during = article.getDuring();
        this.due = article.getDue();
        this.content = article.getContent();
        this.projectImg = projectImg;
        this.recs = recs;
    }
}
