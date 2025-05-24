package com.binario.controller;

import com.binario.converter.JsonConverterHelper;
import com.binario.entity.CourseSection;
import com.binario.entity.SectionsTests;
import com.binario.service.CourseSectionService;
import com.binario.service.SectionsTestsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/teacher/sections/{sectionId}/tests")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherTestController {
    private final SectionsTestsService sectionsTestsService;
    private final CourseSectionService courseSectionService;
    private JsonConverterHelper jsonConverterHelper;

    public TeacherTestController(SectionsTestsService sectionsTestsService,
                                 CourseSectionService courseSectionService,
                                 JsonConverterHelper jsonConverterHelper) {
        this.sectionsTestsService = sectionsTestsService;
        this.courseSectionService = courseSectionService;
        this.jsonConverterHelper = jsonConverterHelper;
    }

    @GetMapping
    public String listTests(@PathVariable Long sectionId,
                            Model model) {
        CourseSection section = courseSectionService.getSectionById(sectionId);
        model.addAttribute("section", section);
        model.addAttribute("tests", sectionsTestsService.getTestBySectionId(sectionId));

        return "teacher/tests/list";
    }

    @GetMapping("/new")
    public String newTestsForm(@PathVariable Long sectionId,
                               Model model) {
        CourseSection section = courseSectionService.getSectionById(sectionId);
        SectionsTests tests = new SectionsTests();
        tests.setSection(section);

        model.addAttribute("section", section);
        model.addAttribute("test", tests);
        model.addAttribute("questionTypes", List.of("SINGLE_CHOICE","MULTIPLE_CHOICE", "TEXT_ANSWER", "CODE_ANSWER"));

        return "teacher/tests/form";
    }

    @PostMapping
    public String createTest(@PathVariable Long sectionId,
                             @ModelAttribute SectionsTests test,
                             RedirectAttributes redirectAttributes) {
        CourseSection section = courseSectionService.getSectionById(sectionId);
        test.setSection(section);
        test.setQuestionType(test.getQuestionType().toLowerCase());

        if(test.getRawAnswerOptions() != null) {
            test.setAnswerOptions(jsonConverterHelper.parseAnswerOptions(test.getRawAnswerOptions()));
        }
        if(test.getRawChoiceAnswers() != null) {
            test.setChoiceAnswers(jsonConverterHelper.parseChoiceAnswers(test.getRawChoiceAnswers()));
        }

        sectionsTestsService.createTests(test);
        redirectAttributes.addFlashAttribute("message", "Тест успешно добавлен");
        return "redirect:/teacher/sections/" + sectionId + "/tests";
    }

    @GetMapping("/{testId}/edit")
    public String editTestForm(@PathVariable Long sectionId,
                               @PathVariable Long testId,
                               Model model) {
        SectionsTests tests = sectionsTestsService.getTestById(testId);

        model.addAttribute("section", tests.getSection());
        model.addAttribute("test", tests);
        model.addAttribute("questionTypes", List.of("SINGLE_CHOICE","MULTIPLE_CHOICE", "TEXT_ANSWER", "CODE_ANSWER"));
        return "teacher/tests/form";
    }

    @PostMapping("/{testId}")
    public String updateTest(@PathVariable Long sectionId,
                             @PathVariable Long testId,
                             @ModelAttribute SectionsTests test,
                             RedirectAttributes redirectAttributes) {
        sectionsTestsService.updateTests(testId, test);
        redirectAttributes.addFlashAttribute("message", "Тест успешно обновлен");

        return "redirect:/teacher/sections/" + sectionId + "/tests";
    }

    @PostMapping("/{testId}/delete")
    public String deleteTest(@PathVariable Long sectionId,
                             @PathVariable Long testId,
                             RedirectAttributes redirectAttributes) {
        sectionsTestsService.deleteTests(testId);
        redirectAttributes.addFlashAttribute("message", "Тест успешно удален");
        return "redirect:/teacher/sections/" + sectionId + "/tests";
    }
}
