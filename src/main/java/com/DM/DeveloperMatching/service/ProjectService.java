package com.DM.DeveloperMatching.service;

import com.DM.DeveloperMatching.domain.*;
import com.DM.DeveloperMatching.dto.Article.ArticleResponse;
import com.DM.DeveloperMatching.dto.Project.ProjectResponse;
import com.DM.DeveloperMatching.dto.Project.ProjectSummary;
import com.DM.DeveloperMatching.dto.Project.TeamMate;
import com.DM.DeveloperMatching.dto.Suggest.SuggestResponse;
import com.DM.DeveloperMatching.dto.User.AppliedUserResponse;
import com.DM.DeveloperMatching.repository.*;
import com.amazonaws.services.s3.AmazonS3;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class ProjectService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final SuggestRepository suggestRepository;
    private final LikesRepository likesRepository;
    @Value("${application.bucket.name}")
    private String bucketName;
    private final AmazonS3 amazonS3;

    //프로젝트 목록 전체 조회
    public List<Project> findAllProjects() {
        return projectRepository.findAll();
    }

    //인기 프로젝트 목록
    public List<ProjectResponse> getPopularProjects() {
        List<Project> projects = projectRepository.findByProjectStatus(ProjectStatus.RECRUITING);

        // 좋아요 수를 기준으로 내림차순으로 프로젝트 정렬
        projects.sort(Comparator.comparingInt(Project::getLikes).reversed());

        // 프로젝트를 ProjectResponse 객체로 매핑
        List<ProjectResponse> popularProjects = projects.stream()
                .map(project -> new ProjectResponse(project, getUrl(project.getArticle())))
                .collect(Collectors.toList());

        return popularProjects;
    }

    //user가 참여한 프로젝트 목록 전체 조회
    public List<ProjectResponse> getAllUserProjects(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("not found"));

        List<Project> projects = user.getUserInMember().stream()    //이 표현 자주 쓰임 잘 기억해 놓을 것
                .map(member -> member.getProject())
                .collect(Collectors.toList());

        List<Project> filteredProjects = projects.stream()
                .filter(project -> project.getProjectStatus() == ProjectStatus.PROCEEDING || project.getProjectStatus() == ProjectStatus.DONE)
                .collect(Collectors.toList());

        List<ProjectResponse> projectResponses = filteredProjects.stream()
                .map(project -> {
                    ProjectResponse responses = new ProjectResponse(project, getUrl(project.getArticle()));
                    return responses;
                })
                .collect(Collectors.toList());

        return projectResponses;
    }

    //user가 참여한 프로젝트 목록 요약 정보 추출
    public List<ProjectSummary> extractSummary(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("not found"));
        List<Project> projects = user.getUserInMember().stream()    //이 표현 자주 쓰임 잘 기억해 놓을 것
                .map(member -> member.getProject())
                .collect(Collectors.toList());

        List<ProjectSummary> projectSummaries = projects.stream()
                .map(project -> {
                    ArticleResponse article = new ArticleResponse(project.getArticle(), getUrl(project.getArticle()));
                    ProjectSummary summary = new ProjectSummary(article);
                    return summary;
                })
                .collect(Collectors.toList());

        return projectSummaries;
    }

    //프로젝트 지원
    public String applyToProject(Long userId, Long projectId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: "));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다: "));

        Member member = Member.builder()
                .memberStatus(MemberStatus.WAITING)
                .user(user)
                .project(project)
                .build();

        memberRepository.save(member);

        return "프로젝트에 지원했습니다.";
    }

    //프로젝트 진행(모집 마감)
    public String proceedProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트가 존재하지 않습니다."));
        project.updateProjectStatus(ProjectStatus.PROCEEDING);
        projectRepository.save(project);

        return "프로젝트를 진행합니다.";
    }

    //프로젝트 종료
    public String terminateProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("not found project"));

        project.updateProjectStatus(ProjectStatus.DONE);
        projectRepository.save(project);

        return "프로젝트가 종료되었습니다.";
    }

    //프로젝트 참여 중인 팀원 조회
    public List<TeamMate> getTeamMates(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("projectId에 해당하는 프로젝트를 찾을 수 없습니다: "));

        List<Member> teamMembers = project.getProjectInMember();

        // 필터링: MemberStatus가 MANAGER 또는 ACCEPTED인 멤버만 선택
        List<TeamMate> teamMates = teamMembers.stream()
                .filter(member -> member.getMemberStatus() == MemberStatus.MANAGER || member.getMemberStatus() == MemberStatus.ACCEPTED)
                .map(member -> new TeamMate(member.getUser().getNickName()))
                .collect(Collectors.toList());

        return teamMates;
    }

    //프로젝트 지원한 인원 조회
    public List<AppliedUserResponse> getAppliedUsers(Long userId, Long projectId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        // userId의 유저가 프로젝트의 주인인지 확인
        if (user==project.getArticle().getArticleOwner()) {
            List<Member> members = project.getProjectInMember();
            System.out.println("get tech by member" + members.get(0).getUser().getTech());
            List<AppliedUserResponse> appliedUsers = members.stream()
                    .filter(member -> member.getMemberStatus() == MemberStatus.WAITING)
                    .map(member -> new AppliedUserResponse(member.getUser(), getUrl(member.getUser())))
                    .collect(Collectors.toList());

            if (!appliedUsers.isEmpty()) {
                System.out.println("get tech by applied user" + appliedUsers.get(0).getTech());
            } else {
                System.out.println("상태가 WAITING인 지원자가 없습니다.");
            }

            return appliedUsers;
        } else {
            // userId의 유저가 프로젝트의 주인이 아닌 경우 예외 처리 또는 다른 로직 수행
            throw new IllegalArgumentException("프로젝트의 주인이 아니므로 조회할 수 없습니다.");
        }
    }

    //프로젝트 참가 요청 수락
    public String acceptJoinProject(Long userId, Long projectId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: "));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다: "));

        Member member = (Member) memberRepository.findByUserAndProject(user, project)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 프로젝트 멤버를 찾을 수 없습니다."));

        if (member.getMemberStatus() == MemberStatus.ACCEPTED) {
            return "이미 참가한 인원입니다.";
        }
        else if (member.getMemberStatus() == MemberStatus.WAITING) {
            member.update(MemberStatus.ACCEPTED);
            memberRepository.save(member);
            return "프로젝트 참가 요청을 수락했습니다.";
        }
        else {
            return "지원하지 않았거나 이미 거절된 인원입니다.";
        }
    }

    // 프로젝트 참가 요청 거절
    public String rejectJoinProject(Long userId, Long projectId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: "));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다: "));

        Member member = (Member) memberRepository.findByUserAndProject(user, project)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 프로젝트 멤버를 찾을 수 없습니다."));
        if (member.getMemberStatus() == MemberStatus.ACCEPTED) {
            return "이미 참가한 인원입니다.";
        }
        else if (member.getMemberStatus() == MemberStatus.WAITING) {
            member.update(MemberStatus.REJECTED);
            memberRepository.delete(member);
            return "프로젝트 참가 요청을 거절했습니다.";
        }
        else {
            return "지원하지 않았거나 이미 거절된 인원입니다.";
        }
    }

    //협업 요청하기
    public String suggestProject(Long userId, Long articleOwnerId, Long projectId) {
        User suggestedUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("요청한 회원을 찾을 수 없습니다."));
        User articleOwner = userRepository.findById(articleOwnerId)
                .orElseThrow(() -> new IllegalArgumentException("모집 글 작성자를 찾을 수 없습니다."));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        Suggest suggest = Suggest.builder()
                .userId(suggestedUser)
                .articleOwner(articleOwner)
                .projectId(project)
                .suggestStatus(MemberStatus.WAITING)
                .build();

        suggestRepository.save(suggest);

        return "협업 요청 되었습니다.";
    }

    // 협업 요청 조회
    public List<SuggestResponse> getSuggestResponses(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Suggest> suggestList = suggestRepository.findByUserId(user);

        List<SuggestResponse> suggestResponses = suggestList.stream()
                .map(suggest -> new SuggestResponse(suggest, getUrl(suggest.getProjectId().getArticle())))
                .collect(Collectors.toList());

        return suggestResponses;
    }

    //협업 요청 수락
    public String acceptSuggestProject(Long suggestId) {
        Suggest suggest = suggestRepository.findById(suggestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 협업 요청을 찾을 수 없습니다."));

        User user = suggest.getUserId();
        Project project = suggest.getProjectId();

        // 이미 해당 유저가 해당 프로젝트의 멤버인지 확인
        Optional<Object> existingMember = memberRepository.findByUserAndProject(user, project);

        // 이미 멤버로 등록된 경우 상태를 'ACCEPTED'로 업데이트
        if (existingMember.isPresent()) {
            Member member = (Member) existingMember.get();
            if(member.getMemberStatus()==MemberStatus.ACCEPTED){
                return "이미 프로젝트 멤버입니다.";
            }
            else{
                member.update(MemberStatus.ACCEPTED);
                memberRepository.save(member);
                suggestRepository.delete(suggest);
                return "프로젝트 멤버로 등록되었습니다.";
            }
        }
        else {
            // 새로운 멤버 등록
            Member newMember = Member.builder()
                    .memberStatus(MemberStatus.ACCEPTED)
                    .user(user)
                    .project(project)
                    .build();
            memberRepository.save(newMember);
            suggestRepository.delete(suggest);
            return "프로젝트 멤버로 등록되었습니다.";
        }

    }

    //협업 요청 거절
    public String rejectSuggestProject(Long suggestId) {
        Suggest suggest = suggestRepository.findById(suggestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 협업 요청을 찾을 수 없습니다."));

        User user = suggest.getUserId();
        Project project = suggest.getProjectId();

        // 이미 해당 유저가 해당 프로젝트의 멤버인지 확인
        Optional<Object> existingMember = memberRepository.findByUserAndProject(user, project);
        if (existingMember.isPresent()) {
            Member member = (Member) existingMember.get();
            if(member.getMemberStatus()==MemberStatus.ACCEPTED){
                return "이미 프로젝트 멤버입니다.";
            }
            else{
                member.update(MemberStatus.REJECTED);
                memberRepository.delete(member);
                suggestRepository.delete(suggest);
                return "요청을 거절했습니다.";
            }
        }
        else {
            suggestRepository.delete(suggest);
            return "요청을 거절했습니다.";
        }
    }

    //프로젝트 좋아요 활성화
    public void activateLike(Long userId, Long projectId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        Likes likes = Likes.builder()
                .likesUser(user)
                .likesProject(project)
                .build();
        likesRepository.save(likes);
        project.increaseLikes();
    }

    //프로젝트 좋아요 비활성화
    public void deactivateLike(Long userId, Long projectId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        Likes likes = likesRepository.findByLikesUserAndLikesProject(user, project);
        likesRepository.delete(likes);
        project.decreaseLikes();
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
