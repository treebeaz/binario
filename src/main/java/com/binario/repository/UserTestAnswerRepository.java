package com.binario.repository;

import com.binario.entity.SectionsTests;
import com.binario.entity.UserTestAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTestAnswerRepository extends JpaRepository<UserTestAnswer, Long> {
    Optional<UserTestAnswer> findByUserIdAndTests(Long userId, SectionsTests tests);
    List<UserTestAnswer> findByUserId(Long userId);
}
