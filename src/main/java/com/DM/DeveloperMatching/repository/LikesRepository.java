package com.DM.DeveloperMatching.repository;

import com.DM.DeveloperMatching.domain.Likes;
import com.DM.DeveloperMatching.domain.Project;
import com.DM.DeveloperMatching.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Likes findByLikesUserAndLikesProject(User user, Project project);
}
