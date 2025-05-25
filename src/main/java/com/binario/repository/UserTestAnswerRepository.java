package com.binario.repository;

import com.binario.entity.SectionsTests;
import com.binario.entity.TestStatus;
import com.binario.entity.User;
import com.binario.entity.UserTestAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTestAnswerRepository extends JpaRepository<UserTestAnswer, Long> {
    Optional<UserTestAnswer> findByUserIdAndTests(Long userId, SectionsTests test);
    boolean existsByUser_IdAndTests_Section_Id(Long userId, Long sectionId);
    List<UserTestAnswer> findByUser_IdAndTests_Section_Id(Long userId, Long sectionId);
    List<UserTestAnswer> findByCodeResultIsNotNullAndIsCorrectFalse();
    @Query("""
        SELECT SUM(uta.score)
        FROM UserTestAnswer uta
        JOIN uta.tests test
        JOIN test.section section
        WHERE uta.user = :user
          AND section.course.id = :courseId
    """)
    Integer findSumScoreByUserAndCourse(@Param("user") User user, @Param("courseId") Long courseId);
    @Query("""
        SELECT SUM(test.maxScore)
        FROM SectionsTests test
        JOIN test.section section
        WHERE section.course.id = :courseId
    """)
    Integer findMaxPossibleScoreForCourse(@Param("courseId") Long courseId);

    @Query("""
        SELECT COALESCE(SUM(uta.score), 0)
        FROM UserTestAnswer uta
        JOIN uta.tests t
        JOIN t.section s
        JOIN s.course c
        WHERE uta.user.id = :userId AND c.id = :courseId
    """)
    Integer sumScoreByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);
}
