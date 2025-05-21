package com.binario.repository;

import com.binario.entity.SectionsTests;
import com.binario.entity.UserTestAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTestAnswerRepository extends JpaRepository<UserTestAnswer, Long> {
    Optional<UserTestAnswer> findByUserIdAndTests(Long userId, SectionsTests test);
    List<UserTestAnswer> findByUserId(Long userId);
    List<UserTestAnswer> findByCodeResultIsNotNullAndIsCorrectFalse();
}
