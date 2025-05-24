package com.binario.repository;

import com.binario.entity.SectionsTests;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionsTestsRepository extends JpaRepository<SectionsTests, Long> {
    List<SectionsTests> findBySectionId(Long sectionId);
}
