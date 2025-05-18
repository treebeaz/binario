package com.binario.repository;

import com.binario.entity.Course;
import com.binario.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserProgressRepository  extends JpaRepository<UserProgress, Long> {
    Optional<UserProgress> findByUserIdAndCourseId(Long userId, Long courseId);

    List<UserProgress> findProgressByCourseId(Long courseId);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    Long course(Course course);
}
