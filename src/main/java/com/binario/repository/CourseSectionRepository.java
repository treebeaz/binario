package com.binario.repository;

import com.binario.entity.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {
    List<CourseSection> findByCourseId(Long courseId);
    int countSectionByChapterId(Long courseId);
    List<CourseSection> findByChapterIdOrderByOrderAsc(Long chapterId);
    @Query("SELECT MAX(s.order) FROM CourseSection s WHERE s.chapter.id = :chapterId")
    Integer findMaxOrderByChapterId(@Param("chapterId") Long chapterId);


}
