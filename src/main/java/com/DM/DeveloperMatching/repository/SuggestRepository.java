package com.DM.DeveloperMatching.repository;

import com.DM.DeveloperMatching.domain.Suggest;
import com.DM.DeveloperMatching.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SuggestRepository extends JpaRepository<Suggest, Long> {
    List<Suggest> findByUserId(User user);
}
