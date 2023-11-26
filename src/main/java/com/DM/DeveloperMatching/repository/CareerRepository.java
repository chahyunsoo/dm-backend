package com.DM.DeveloperMatching.repository;

import com.DM.DeveloperMatching.domain.Career;
import com.DM.DeveloperMatching.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CareerRepository extends JpaRepository<Career, Long> {
    boolean existsByUserAndContent(User user, String content);
    @Modifying
    @Query("DELETE FROM Career c WHERE c.user.uId = :userId AND c.content = :content")
    void deleteByUserIdANDContent(@Param(value = "userId") Long userId, @Param(value = "content") String content);
}