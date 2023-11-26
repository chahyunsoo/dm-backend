package com.DM.DeveloperMatching.controller;

import com.DM.DeveloperMatching.config.jwt.JwtTokenUtils;
import com.DM.DeveloperMatching.domain.Article;
import com.DM.DeveloperMatching.domain.User;
import com.DM.DeveloperMatching.dto.Article.AddArticleRequest;
import com.DM.DeveloperMatching.dto.Article.ArticleResponse;
import com.DM.DeveloperMatching.dto.Article.UpdateArticleRequest;
import com.DM.DeveloperMatching.service.ArticleService;
import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
@CrossOrigin("http://localhost:3000/")
public class ArticleController {

    private final ArticleService articleService;
    private final JwtTokenUtils jwtTokenUtils;
    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${application.bucket.name}")
    private String bucketName;
    private final AmazonS3 amazonS3;

    //모집 글 생성
    @PostMapping("/articles")
    public ResponseEntity<ArticleResponse> createArticle(@RequestHeader HttpHeaders headers,
                                                         @RequestPart AddArticleRequest articleRequest,
                                                         @RequestPart MultipartFile projectImg) throws IOException {
        String token = headers.getFirst("Authorization");
        Long uId = jwtTokenUtils.extractUserId(token,secretKey);

        Article savedArticle = articleService.save(articleRequest, uId, projectImg);
        ArticleResponse articleResponse = new ArticleResponse(savedArticle, getUrl(savedArticle));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(articleResponse);
    }

    //모집 글 목록 조회
    @GetMapping("/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {

        List<Article> articles = articleService.findAll();
        List<ArticleResponse> articleResponses = articles.stream()
                .map(article -> new ArticleResponse(article, getUrl(article)))
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .body(articleResponses);
    }

    //모집 글 단건 조회
    @GetMapping("/articles/{aId}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long aId) {
        Article article = articleService.findOne(aId);
        ArticleResponse articleResponse = new ArticleResponse(article, getUrl(article));

        return ResponseEntity.ok()
                .body(articleResponse);
    }

    //모집 글 수정
    @PutMapping("/articles/{aId}")
    public ResponseEntity<ArticleResponse> updateArticle(@PathVariable long aId,
                                                         @RequestPart UpdateArticleRequest request,
                                                         @RequestPart(required = false) MultipartFile projectImg)
            throws IOException {
        Article updatedArticle = articleService.update(aId, request, projectImg);
        ArticleResponse updatedArticleResponse = new ArticleResponse(updatedArticle, getUrl(updatedArticle));

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
}
