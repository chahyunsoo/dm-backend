package com.DM.DeveloperMatching.controller;

import com.DM.DeveloperMatching.config.jwt.JwtTokenUtils;
import com.DM.DeveloperMatching.domain.Article;
import com.DM.DeveloperMatching.domain.User;
import com.DM.DeveloperMatching.dto.Article.AddArticleRequest;
import com.DM.DeveloperMatching.dto.Article.ArticleResponse;
import com.DM.DeveloperMatching.dto.Article.ArticleWithRecResponse;
import com.DM.DeveloperMatching.dto.Article.UpdateArticleRequest;
import com.DM.DeveloperMatching.dto.Recommend.RecommendUserDto;
import com.DM.DeveloperMatching.service.ArticleService;
import com.DM.DeveloperMatching.service.RecommendService;
import com.DM.DeveloperMatching.service.UserService;
import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class ArticleController {

    private final ArticleService articleService;
    private final RecommendService recommendService;
    private final JwtTokenUtils jwtTokenUtils;
    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${application.bucket.name}")
    private String bucketName;
    private final AmazonS3 amazonS3;

    //모집 글 생성
    @PostMapping(value = "/articles")
    public ResponseEntity<ArticleResponse> createArticle(@RequestHeader HttpHeaders headers,
                                                         @ModelAttribute AddArticleRequest articleRequest)
            throws IOException {
        String token = headers.getFirst("Authorization");
        Long uId = jwtTokenUtils.extractUserId(token,secretKey);

        Article savedArticle = articleService.save(articleRequest, uId, articleRequest.getProjectImg());
        ArticleResponse articleResponse = new ArticleResponse(savedArticle, getUrl(savedArticle),
                getUrl(savedArticle.getArticleOwner()));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(articleResponse);
    }

    //모집 글 목록 조회
    @GetMapping("/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {

        List<Article> articles = articleService.findAll();
        List<ArticleResponse> articleResponses = articles.stream()
                .map(article -> new ArticleResponse(article, getUrl(article), getUrl(article.getArticleOwner())))
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .body(articleResponses);
    }

    //모집 글 단건 조회
    @GetMapping("/articles/{aId}")
    public ResponseEntity<ArticleWithRecResponse> findArticle(@PathVariable long aId) {
        Article article = articleService.findOne(aId);
        List<User> users = recommendService.recommendUserByCS(aId).get(0);
        List<RecommendUserDto> dtos = users.stream()
                .map(user -> user.toDto(user, getUrl(user)))
                .collect(Collectors.toList());
        List<RecommendUserDto> real = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            try {
                real.add(dtos.get(i));
            } catch (Exception e) {
                break;
            }
        }
        ArticleWithRecResponse articleWithRecResponse = new ArticleWithRecResponse(article, getUrl(article),
                getUrl(article.getArticleOwner()), real);

        return ResponseEntity.ok()
                .body(articleWithRecResponse);
    }

    //모집 글 수정
    @PutMapping(value = "/articles/{aId}")
    public ResponseEntity<ArticleResponse> updateArticle(@PathVariable long aId,
                                                         @ModelAttribute UpdateArticleRequest request)
            throws IOException {
        Article updatedArticle = articleService.update(aId, request, request.getProjectImg());
        ArticleResponse updatedArticleResponse = new ArticleResponse(updatedArticle, getUrl(updatedArticle),
                getUrl(updatedArticle.getArticleOwner()));

        return ResponseEntity.ok()
                .body(updatedArticleResponse);
    }

    //모집 글 삭제
    @DeleteMapping("/articles/{aId}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long aId) {
        articleService.delete(aId);

        return ResponseEntity.ok()
                .build();
    }

    private String getUrl(Article article) {
        URL url = amazonS3.getUrl(bucketName, article.getProjectImg());
        String urltext = "" + url;
        return urltext;
    }

    public String getUrl(User user) {
        URL url = amazonS3.getUrl(bucketName, user.getUserImg());
        String urltext = "" + url;
        return urltext;
    }
}
