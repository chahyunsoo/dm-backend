package com.DM.DeveloperMatching.service;

import com.DM.DeveloperMatching.domain.*;
import com.DM.DeveloperMatching.dto.Article.AddArticleRequest;
import com.DM.DeveloperMatching.dto.Article.ArticleResponse;
import com.DM.DeveloperMatching.dto.Article.UpdateArticleRequest;
import com.DM.DeveloperMatching.repository.ArticleRepository;
import com.DM.DeveloperMatching.repository.MemberRepository;
import com.DM.DeveloperMatching.repository.UserRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    @Value("${application.bucket.name}")
    private String bucket;
    private final AmazonS3 amazonS3;

    //모집 글 생성
    public Article save(AddArticleRequest articleRequest, Long userId, MultipartFile projectImg) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("not found user"));

        Article savedArticle = articleRepository.save(articleRequest.toEntity(user));

        if(projectImg != null) {
            savedArticle.setProjectImg(upload(projectImg));
        }

        Member member = Member.builder()
                .memberStatus(MemberStatus.MANAGER)
                .user(user)
                .project(savedArticle.getProject())
                .build();
        memberRepository.save(member);

        return savedArticle;
    }

    //모집 글 목록 조회
    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    //모집 글 단건 조회
    public Article findOne(Long userId) {
        Article article = articleRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("not found article"));

        return article;
    }

    //모집 글 수정
    public Article update(Long articleId, UpdateArticleRequest updateArticleRequest, MultipartFile projectImg) throws IOException {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("not found article"));

        String result1 = String.join(", ", updateArticleRequest.getRecPart());
        String result2 = String.join(", ", updateArticleRequest.getRecTech());

        if(projectImg != null) {
            article.update(updateArticleRequest.getTitle(), updateArticleRequest.getMaximumMember(),
                    result1, result2,
                    updateArticleRequest.getRecLevel(), updateArticleRequest.getDuring(), updateArticleRequest.getDue()
                    , updateArticleRequest.getContent(), upload(projectImg));
        }
        else {
            article.update(updateArticleRequest.getTitle(), updateArticleRequest.getMaximumMember(),
                    result1, result2,
                    updateArticleRequest.getRecLevel(), updateArticleRequest.getDuring(), updateArticleRequest.getDue()
                    , updateArticleRequest.getContent());
        }
        return articleRepository.save(article);
    }

    //모집 글 삭제
    public void delete(Long articleId) {
        articleRepository.deleteById(articleId);
    }

    public String upload(MultipartFile multipartFile) throws IOException {
        String s3FileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentLength(multipartFile.getInputStream().available());

        amazonS3.putObject(bucket, s3FileName, multipartFile.getInputStream(), objMeta);

        return s3FileName;
    }
}
