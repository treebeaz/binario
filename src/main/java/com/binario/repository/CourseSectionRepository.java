package com.binario.repository;

import com.binario.entity.Course;
import com.binario.entity.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseSectionRepository  extends JpaRepository<CourseSection, Long> {
    List<CourseSection> findByCourseIdOrderByPositionAsc(Long courseId);
    Optional<CourseSection> findById(Long sectionId);

    long countByCourseId(Long courseId);

}
