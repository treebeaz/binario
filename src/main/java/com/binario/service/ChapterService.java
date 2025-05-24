package com.binario.service;

import com.binario.entity.Chapter;
import com.binario.repository.ChapterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChapterService{
    private final ChapterRepository chapterRepository;

    public ChapterService(ChapterRepository chapterRepository) {
        this.chapterRepository = chapterRepository;
    }

    public List<Chapter> getChaptersByCourseId(Long courseId) {
        return chapterRepository.findByCourseId(courseId);
    }

    public Chapter getChapterById(Long id) {
        return chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Глава не найдена"));
    }

    @Transactional
    public void saveChapter(Chapter newChapter) {
        Integer nextOrder = getNextOrderNumber(newChapter.getCourse().getId());
        
        Chapter chapter = new Chapter();
        chapter.setTitle(newChapter.getTitle());
        chapter.setDescription(newChapter.getDescription());
        chapter.setOrder(nextOrder);
        chapter.setCourse(newChapter.getCourse());
        chapterRepository.save(chapter);
    }

    private Integer getNextOrderNumber(Long courseId) {
        Integer maxOrder = chapterRepository.findMaxOrderByCourseId(courseId);
        return (maxOrder == null) ? 1 : maxOrder + 1;
    }
}
