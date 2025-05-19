package com.binario.repository;

import com.binario.entity.Course;
import com.binario.entity.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {
    List<CourseSection> findByCourseId(Long courseId);
//    Optional<CourseSection> findById(Long sectionId);

    List<CourseSection> findByChapterId(Long chapterId);

    long countByCourseId(Long courseId);
}
