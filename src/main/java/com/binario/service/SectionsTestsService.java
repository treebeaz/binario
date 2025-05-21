package com.binario.service;

import com.binario.entity.SectionsTests;
import com.binario.repository.SectionsTestsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectionsTestsService {
    private final SectionsTestsRepository sectionsTestsRepository;

    public SectionsTestsService(SectionsTestsRepository sectionsTestsRepository) {
        this.sectionsTestsRepository = sectionsTestsRepository;
    }

    public List<SectionsTests> getTestBySection(Long sectionId) {
        return sectionsTestsRepository.findBySectionId(sectionId);
    }

    public SectionsTests getTestById(Long sectionId) {
        return sectionsTestsRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Test not found"));
    }

    public SectionsTests save(SectionsTests sectionsTests) {
        return sectionsTestsRepository.save(sectionsTests);
    }

    public void delete(SectionsTests sectionsTests) {
        sectionsTestsRepository.delete(sectionsTests);
    }
}
