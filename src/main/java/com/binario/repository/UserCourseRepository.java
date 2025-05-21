package com.binario.repository;

import com.binario.entity.UserCourse;
import com.binario.entity.UserCourseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCourseRepository extends JpaRepository<UserCourse, UserCourseId> {
    List<UserCourse> findByUserId(Long userId);
    List<UserCourse> findByCourseId(Long courseId);
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    @Query("SELECT uc FROM UserCourse uc WHERE uc.user.id = :userId")
    List<UserCourse> findEnrolledCoursesByUserId(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(uc) > 0 THEN true ELSE false END FROM UserCourse uc WHERE uc.user.id = :userId AND uc.course.id = :courseId")
    boolean isUserEnrolledInCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);
}