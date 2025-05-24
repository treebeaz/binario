package com.binario.service;

import com.binario.entity.SectionsTests;
import com.binario.repository.SectionsTestsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SectionsTestsService {
    private final SectionsTestsRepository sectionsTestsRepository;

    public SectionsTestsService(SectionsTestsRepository sectionsTestsRepository) {
        this.sectionsTestsRepository = sectionsTestsRepository;
    }

    public List<SectionsTests> getTestBySectionId(Long sectionId) {
        return sectionsTestsRepository.findBySectionId(sectionId);
    }

    public SectionsTests getTestById(Long sectionId) {
        return sectionsTestsRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Test not found"));
    }

    public void createTests(SectionsTests sectionsTests) {
        sectionsTestsRepository.save(sectionsTests);
    }

    public void updateTests(Long testId, SectionsTests sectionsTests) {
        SectionsTests test = getTestById(testId);

        test.setSection(sectionsTests.getSection());
        test.setQuestionType(sectionsTests.getQuestionType());
        test.setQuestionText(sectionsTests.getQuestionText());
        test.setAnswerOptions(sectionsTests.getAnswerOptions());
        test.setTextAnswer(sectionsTests.getTextAnswer());
        test.setCode(sectionsTests.getCode());
        test.setTestCases(sectionsTests.getTestCases());
        test.setMaxScore(sectionsTests.getMaxScore());
        test.setSortOrder(sectionsTests.getSortOrder());

        sectionsTestsRepository.save(test);
    }

    public void deleteTests(Long testId) {
        sectionsTestsRepository.deleteById(testId);
    }


}
