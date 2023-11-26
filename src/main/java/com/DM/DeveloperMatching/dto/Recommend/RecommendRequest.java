package com.DM.DeveloperMatching.dto.Recommend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RecommendRequest {
    private List<String> recPart;
    private List<String> recTech;
    private List<String> recLevel;
}