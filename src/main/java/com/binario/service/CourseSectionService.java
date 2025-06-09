package com.binario.service;

import com.binario.entity.Chapter;
import com.binario.entity.CourseSection;
import com.binario.repository.ChapterRepository;
import com.binario.repository.CourseSectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CourseSectionService {

    /**
     * Класс-сервис для работы с разделами курса
     */

    private final CourseSectionRepository courseSectionRepository;
    private final ChapterRepository chapterRepository;


    public CourseSectionService(CourseSectionRepository courseSectionRepository, ChapterRepository chapterRepository) {
        this.courseSectionRepository = courseSectionRepository;
        this.chapterRepository = chapterRepository;
    }

    public List<CourseSection> getAllSectionsByChapterId(Chapter chapter) {
        return courseSectionRepository.findByChapterIdOrderByOrderAsc(chapter.getId());
    }

    public List<Chapter> getChaptersWithSections(Long courseId) {
        return chapterRepository.findByCourseIdWithSections(courseId);
    }


    public CourseSection getSectionById(Long sectionId) {
        return courseSectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));
    }

    @Transactional
    public void saveSection(CourseSection newSection) {
        if(newSection.getId() != null) {
            courseSectionRepository.save(newSection);
        }
        else {
            Integer nextOrder = getNextOrderNumber(newSection.getChapter().getId());

            CourseSection savedSection = new CourseSection();
            savedSection.setTitle(newSection.getTitle());
            savedSection.setDescription(newSection.getDescription());
            savedSection.setCourse(newSection.getCourse());
            savedSection.setContent(newSection.getContent());
            savedSection.setChapter(newSection.getChapter());
            savedSection.setOrder(nextOrder);
            courseSectionRepository.save(savedSection);
        }
    }

    private Integer getNextOrderNumber(Long chapterId) {
        Integer maxOrder = courseSectionRepository.findMaxOrderByChapterId(chapterId);
        return (maxOrder == null) ? 1 : maxOrder + 1;
    }
}
