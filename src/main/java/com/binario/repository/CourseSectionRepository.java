package com.binario.repository;

import com.binario.entity.Course;
import com.binario.entity.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {
    List<CourseSection> findByCourseId(Long courseId);

    List<CourseSection> findByChapterId(Long chapterId);

    int countSectionByChapterId(Long courseId);

    List<CourseSection> findByChapterIdOrderByOrderAsc(Long chapterId);
    
    @Query("SELECT MAX(s.order) FROM CourseSection s WHERE s.chapter.id = :chapterId")
    Integer findMaxOrderByChapterId(@Param("chapterId") Long chapterId);


}
