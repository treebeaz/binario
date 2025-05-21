package com.binario.repository;

import com.binario.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findByCourseId(Long courseId);
    @Query("SELECT DISTINCT c FROM Chapter c LEFT JOIN FETCH c.sections WHERE c.course.id = :courseId ORDER BY c.order")
    List<Chapter> findByCourseIdWithSections(@Param("courseId") Long courseId);
    @Query("SELECT MAX(c.order) FROM Chapter c WHERE c.course.id = :courseId")
    Integer findMaxOrderByCourseId(@Param("courseId") Long courseId);
}