package com.DM.DeveloperMatching.repository;

import com.DM.DeveloperMatching.domain.History;
import com.DM.DeveloperMatching.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HistoryRepository extends JpaRepository<History, Long> {
    boolean existsByUserAndTitle(User user, String Title);
    @Modifying
    @Query("DELETE FROM History h WHERE h.user.uId = :userId AND h.title = :title")
    void deleteByUserIdANDTitle(@Param(value = "userId") Long userId, @Param(value = "title") String title);
}