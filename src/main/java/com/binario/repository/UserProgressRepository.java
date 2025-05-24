package com.binario.repository;

import com.binario.entity.Course;
import com.binario.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProgressRepository  extends JpaRepository<UserProgress, Long> {
    Long course(Course course);
}
