package com.DM.DeveloperMatching.controller;

import com.DM.DeveloperMatching.config.jwt.JwtTokenUtils;
import com.DM.DeveloperMatching.domain.Article;
import com.DM.DeveloperMatching.domain.User;
import com.DM.DeveloperMatching.dto.Recommend.RecommendProjectDto;
import com.DM.DeveloperMatching.dto.Recommend.RecommendRequest;
import com.DM.DeveloperMatching.dto.Recommend.RecommendUserDto;
import com.DM.DeveloperMatching.service.RecommendService;
import com.DM.DeveloperMatching.service.UserService;
import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class RecommendController {
    private final RecommendService recommendService;
    private final JwtTokenUtils jwtTokenUtils;
    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${application.bucket.name}")
    private String bucketName;
    private final AmazonS3 amazonS3;

    @GetMapping(value = "/rec-project")
    public ResponseEntity<List<RecommendProjectDto>> recommendProject(@RequestHeader HttpHeaders headers) {
        String token = headers.getFirst("Authorization");
        Long userId = jwtTokenUtils.extractUserId(token, secretKey);
        List<Article> articles = recommendService.recommendProjectByCS(userId);
        List<RecommendProjectDto> recommendDtos = articles.stream()
                .map(article -> article.toDto(article, getUrl(article)))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK)
                .body(recommendDtos);
    }

    @PostMapping(value = "/rec-project")
    public ResponseEntity<List<RecommendProjectDto>> recommendProject(@RequestHeader HttpHeaders headers,
                                                                      @RequestBody(required = false) RecommendRequest request) {
        String token = headers.getFirst("Authorization");
        Long userId = jwtTokenUtils.extractUserId(token, secretKey);
        List<Article> articles = recommendService.recommendProjectByCS(userId, request.getRecPart(),
                request.getRecTech(),
                request.getRecLevel());

        List<RecommendProjectDto> recommendDtos = articles.stream()
                .map(article -> article.toDto(article, getUrl(article)))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK)
                .body(recommendDtos);

    }

    @GetMapping(value = "/rec-teammate/{id}")
    public ResponseEntity<Map<String, List<RecommendUserDto>>> recommendUser(@PathVariable Long id) {
        List<List<User>> users = recommendService.recommendUserByCS(id);
        List<String> parts = new ArrayList<>();
        List<List<RecommendUserDto>> dtos = new ArrayList<>();
        Map<String, List<RecommendUserDto>> result = new HashMap<>();
        for(List<User> userList : users) {
//            if(!result.containsKey(userList.get(0).getPart())) {
//                result.put(userList.get(0).getPart(), userList.stream()
//                        .map(user -> user.toDto(user, getUrl(user)))
//                        .collect(Collectors.toList()));
//            }
//            else {
//                result.get(userList.get(0).getPart()).addAll(userList.stream()
//                        .map(user -> user.toDto(user, getUrl(user)))
//                        .collect(Collectors.toList()));
//            }
            parts.add(userList.get(0).getPart());
            dtos.add(userList.stream()
                        .map(user -> user.toDto(user, getUrl(user)))
                        .collect(Collectors.toList()));
        }
        for(int i = 0; i < parts.size(); i++) {
            result.put(parts.get(i), dtos.get(i));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    private String getUrl(Article article) {
        URL url = amazonS3.getUrl(bucketName, article.getProjectImg());
        String urltext = "" + url;
        return urltext;
    }

    private String getUrl(User user) {
        URL url = amazonS3.getUrl(bucketName, user.getUserImg());
        String urltext = "" + url;
        return urltext;
    }
}