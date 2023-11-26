package com.DM.DeveloperMatching.repository;

import com.DM.DeveloperMatching.domain.Member;
import com.DM.DeveloperMatching.domain.Project;
import com.DM.DeveloperMatching.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Object> findByUserAndProject(User user, Project project);
}
