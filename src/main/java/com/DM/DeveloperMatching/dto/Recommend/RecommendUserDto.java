package com.DM.DeveloperMatching.dto.Recommend;

import com.DM.DeveloperMatching.domain.Article;
import com.DM.DeveloperMatching.domain.Level;
import com.DM.DeveloperMatching.domain.User;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class RecommendUserDto {
    private Long uId;
    private String nickName;
    private String email;
    private String part;
    private List<String> tech;
    private Level level;
    private Double point;
    private String introduction;
    private String userImg;
}