package com.DM.DeveloperMatching.repository;

import com.DM.DeveloperMatching.domain.Project;
import com.DM.DeveloperMatching.domain.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByProjectStatus(ProjectStatus recruiting);
}
