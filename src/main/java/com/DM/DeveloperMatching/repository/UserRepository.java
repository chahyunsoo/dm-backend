package com.DM.DeveloperMatching.repository;

import com.DM.DeveloperMatching.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String userName);

    User findByNickName(String nickName);

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    boolean existsByNickName(String nickName);

    Optional<User> findByEmail(String email);

    List<User> findAllByPart(String part);
}