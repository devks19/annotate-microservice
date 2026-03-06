package com.annotate.auth.repository;

import com.annotate.auth.entity.User;
import com.annotate.auth.enums.UserRole;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByTeamId(Long teamId);
    List<User> findByRole(UserRole role);

}