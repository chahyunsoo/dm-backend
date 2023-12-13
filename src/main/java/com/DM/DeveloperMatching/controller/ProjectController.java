package com.DM.DeveloperMatching.controller;

import com.DM.DeveloperMatching.config.jwt.JwtTokenUtils;
import com.DM.DeveloperMatching.dto.Project.ProjectResponse;
import com.DM.DeveloperMatching.dto.Project.TeamMate;
import com.DM.DeveloperMatching.dto.Suggest.SuggestResponse;
import com.DM.DeveloperMatching.dto.User.AppliedUserResponse;
import com.DM.DeveloperMatching.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class ProjectController {

    private final ProjectService projectService;
    private final JwtTokenUtils jwtTokenUtils;
    @Value("${jwt.secret-key}")
    private String secretKey;

    //프로젝트 지원
    @PostMapping("/project/{pId}/apply")
    public ResponseEntity<String> applyToProject(@RequestHeader HttpHeaders headers,
                                                 @PathVariable long pId) {
        String token = headers.getFirst("Authorization");
        Long uId = jwtTokenUtils.extractUserId(token,secretKey);
        String result = projectService.applyToProject(uId, pId);

        return ResponseEntity.ok()
                .body(result);
    }

    //프로젝트 진행(모집 마감)
    @PostMapping("/project/{pId}/proceed")
    public ResponseEntity<String> proceedProject(@PathVariable long pId) {
        String result = projectService.proceedProject(pId);

        return ResponseEntity.ok()
                .body(result);
    }

    //프로젝트 종료
    @PostMapping("/project/{pId}/terminate")
    public ResponseEntity<String> terminateProject(@PathVariable long pId) {
        String result = projectService.terminateProject(pId);

        return ResponseEntity.ok()
                .body(result);
    }

    //프로젝트 팀원 조회
    @GetMapping("/project/{pId}/get-teammates")
    public ResponseEntity<List<TeamMate>> getTeamMates(@PathVariable long pId) {
        List<TeamMate> teamMates = projectService.getTeamMates(pId);

        return ResponseEntity.ok()
                .body(teamMates);
    }

    //인기 프로젝트 조회
    @GetMapping("/project/get-pop-projects")
    public ResponseEntity<List<ProjectResponse>> getPopularProjects() {
        List<ProjectResponse> popularProjects = projectService.getPopularProjects();

        return ResponseEntity.ok()
                .body(popularProjects);
    }

    //프로젝트 지원한 인원 조회
    @GetMapping("/project/{pId}/get-apply-request")
    public ResponseEntity<List<AppliedUserResponse>> getAppliedUsers(@RequestHeader HttpHeaders headers,
                                                                     @PathVariable long pId) {
        String token = headers.getFirst("Authorization");
        Long uId = jwtTokenUtils.extractUserId(token,secretKey);
        List<AppliedUserResponse> appliedUsers = projectService.getAppliedUsers(uId, pId);

        return ResponseEntity.ok()
                .body(appliedUsers);
    }

    //프로젝트 참가 요청 수락
    @PostMapping("/project/{pId}/{uId}/accept-join-project")
    public ResponseEntity<String> acceptJoinProject(@PathVariable long uId,
                                                    @PathVariable long pId) {
        String result = projectService.acceptJoinProject(uId, pId);

        return ResponseEntity.ok()
                .body(result);
    }

    // 프로젝트 참가 요청 거절
    @PostMapping("/project/{pId}/{uId}/reject-join-project")
    public ResponseEntity<String> rejectJoinProject(@PathVariable long uId,
                                                    @PathVariable long pId) {
        String result = projectService.rejectJoinProject(uId, pId);

        return ResponseEntity.ok()
                .body(result);
    }

    //협업요청
    @PostMapping("/project/{pId}/{uId}/suggest-project")
    public ResponseEntity<String> suggestProject(@RequestHeader HttpHeaders headers,
                                                 @PathVariable long uId,
                                                 @PathVariable long pId) {
        String token = headers.getFirst("Authorization");
        Long aoId = jwtTokenUtils.extractUserId(token, secretKey);
        String result = projectService.suggestProject(uId, aoId, pId);

        return ResponseEntity.ok()
                .body(result);
    }

    //협업 요청 조회
    @GetMapping("/project/get-suggest-project")
    public ResponseEntity<List<SuggestResponse>> getSuggestResponses(@RequestHeader HttpHeaders headers) {
        String token = headers.getFirst("Authorization");
        Long uId = jwtTokenUtils.extractUserId(token, secretKey);
        List<SuggestResponse> suggestResponses = projectService.getSuggestResponses(uId);

        return ResponseEntity.ok()
                .body(suggestResponses);
    }

    //협업 요청 수락
    @PostMapping("/project/{sId}/accept-suggest-project")
    public ResponseEntity<String> acceptSuggestProject(@PathVariable long sId) {
        String result = projectService.acceptSuggestProject(sId);

        return ResponseEntity.ok()
                .body(result);
    }

    //협업 요청 거절
    @PostMapping("/project/{sId}/reject-suggest-project")
    public ResponseEntity<String> rejectSuggestProject(@PathVariable long sId) {
        String result = projectService.rejectSuggestProject(sId);

        return ResponseEntity.ok()
                .body(result);
    }

    //프로젝트 좋아요 활성화
    @PostMapping("/project/{pId}/activate-likes")
    public ResponseEntity<Void> activateLike(@RequestHeader HttpHeaders headers,
                                             @PathVariable long pId) {
        String token = headers.getFirst("Authorization");
        Long uId = jwtTokenUtils.extractUserId(token, secretKey);
        projectService.activateLike(uId, pId);

        return ResponseEntity.ok()
                .build();
    }

    //프로젝트 좋아요 비활성화
    @PostMapping("/project/{pId}/deactivate-likes")
    public ResponseEntity<Void> deactivateLike(@RequestHeader HttpHeaders headers,
                                             @PathVariable long pId) {
        String token = headers.getFirst("Authorization");
        Long uId = jwtTokenUtils.extractUserId(token, secretKey);
        projectService.deactivateLike(uId, pId);

        return ResponseEntity.ok()
                .build();
    }

}
