package com.DM.DeveloperMatching.repository;

import com.DM.DeveloperMatching.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}