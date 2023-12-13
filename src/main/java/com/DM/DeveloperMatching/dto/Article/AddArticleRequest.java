package com.DM.DeveloperMatching.dto.Article;

import com.DM.DeveloperMatching.domain.Article;
import com.DM.DeveloperMatching.domain.Level;
import com.DM.DeveloperMatching.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddArticleRequest {
    private User articleOwner;
    private String title;
    private Integer maximumMember;
    private List<String> recPart;
    private List<String> recTech;
    private Level recLevel;
    private String during;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date due;
    private String content;
    private MultipartFile projectImg;

    public Article toEntity(User user) {
        String recPart = String.join(", ", this.recPart);
        String recTech = String.join(", ", this.recTech);
        return Article.builder()
                .articleOwner(user)
                .title(title)
                .maximumMember(maximumMember)
                .recPart(recPart)
                .recTech(recTech)
                .during(during)
                .due(due)
                .recLevel(recLevel)
                .content(content)
                .build();
    }
}