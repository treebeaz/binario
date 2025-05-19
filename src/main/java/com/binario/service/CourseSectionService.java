package com.binario.service;

import com.binario.entity.Chapter;
import com.binario.entity.CourseSection;
import com.binario.entity.UserProgress;
import com.binario.repository.ChapterRepository;
import com.binario.repository.CourseSectionRepository;
import com.binario.repository.UserProgressRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseSectionService {
    private final CourseSectionRepository courseSectionRepository;
    private final ChapterRepository chapterRepository;


    public CourseSectionService(CourseSectionRepository courseSectionRepository, ChapterRepository chapterRepository) {
        this.courseSectionRepository = courseSectionRepository;
        this.chapterRepository = chapterRepository;
    }

    public List<CourseSection> getSectionsByCourseId(Long courseId) {
        return courseSectionRepository.findByCourseId(courseId);
    }

    public List<CourseSection> getAllSectionsByChapterId(Chapter chapter) {
        return courseSectionRepository.findByChapterId(chapter.getId());
    }

    public List<Chapter> getChaptersWithSections(Long courseId) {
        return chapterRepository.findByCourseIdWithSections(courseId);
    }

    public boolean isLastSection(Long courseId, int position) {
        long totalSections = courseSectionRepository.countByCourseId(courseId);
        return position >= totalSections;
    }

    public CourseSection getSectionById(Long sectionId) {
        return courseSectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));
    }
}
