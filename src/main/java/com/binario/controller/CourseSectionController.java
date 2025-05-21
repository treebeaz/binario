package com.binario.controller;

import com.binario.entity.*;
import com.binario.repository.ChapterRepository;
import com.binario.repository.CourseRepository;
import com.binario.service.CourseSectionService;
import com.binario.service.SectionsTestsService;
import com.binario.service.UserService;
import com.binario.service.UserTestAnswerService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/courses/{courseId}/sections")
public class CourseSectionController {

    private final CourseSectionService courseSectionService;
    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;
    private final SectionsTestsService sectionsTestsService;
    private final UserTestAnswerService userTestAnswerService;
    private final UserService userService;

    public CourseSectionController(CourseSectionService courseSectionService,
                                   CourseRepository courseRepository,
                                   ChapterRepository chapterRepository,
                                   SectionsTestsService sectionsTestsService,
                                   UserTestAnswerService userTestAnswerService,
                                   UserService userService) {

        this.courseSectionService = courseSectionService;
        this.courseRepository = courseRepository;
        this.chapterRepository = chapterRepository;
        this.sectionsTestsService = sectionsTestsService;
        this.userTestAnswerService = userTestAnswerService;
        this.userService = userService;
    }

    @GetMapping
    public String showChapters(@AuthenticationPrincipal User user,
                               @PathVariable Long courseId,
                               Model model) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));


        List<Chapter> chapters = courseSectionService.getChaptersWithSections(courseId);

        Map<Long, List<SectionsTests>> testsBySection = new HashMap<>();
        for (Chapter chapter : chapters) {
            for (CourseSection section : chapter.getSections()) {
                List<SectionsTests> tests = sectionsTestsService.getTestBySection(section.getId());
                if (tests != null && !tests.isEmpty()) {
                    testsBySection.put(section.getId(), tests);
                }
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("course", course);
        model.addAttribute("chapters", chapters);
        model.addAttribute("testsBySection", testsBySection);

        return "sections/chapters";
    }

    @GetMapping("/chapter/{chapterId}")
    public String showChapterContent(@AuthenticationPrincipal User user,
                                     @PathVariable Long courseId,
                                     @PathVariable Long chapterId,
                                     Model model) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));

        List<CourseSection> sections = courseSectionService.getAllSectionsByChapterId(chapter);

        model.addAttribute("user", user);
        model.addAttribute("course", course);
        model.addAttribute("chapter", chapter);
        model.addAttribute("sections", sections);

        return "sections/chapter-content";
    }

    @GetMapping("/{sectionId}")
    public String showSectionContent(@AuthenticationPrincipal User user,
                                     @PathVariable Long courseId,
                                     @PathVariable Long sectionId,
                                     Model model) {
        try {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            CourseSection section = courseSectionService.getSectionById(sectionId);

            model.addAttribute("user", user);
            model.addAttribute("course", course);
            model.addAttribute("section", section);
            return "sections/section-content";
        }
        catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/{sectionId}/tests")
    public String showTests(@AuthenticationPrincipal User user,
                            @PathVariable Long courseId,
                            @PathVariable Long sectionId,
                            Model model) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        CourseSection section = courseSectionService.getSectionById(sectionId);

        List<SectionsTests> tests = sectionsTestsService.getTestBySection(sectionId);

        model.addAttribute("user", user);
        model.addAttribute("course", course);
        model.addAttribute("section", section);
        model.addAttribute("tests", tests);

        return "sections/sections-tests";
    }

    @PostMapping("/{sectionId}/tests/submit")
    public String submitTest(@AuthenticationPrincipal UserDetails userDetails,
                             @PathVariable Long courseId,
                             @PathVariable Long sectionId,
                             @RequestParam MultiValueMap<String, String> allRequestParams,
                             RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<SectionsTests> tests = sectionsTestsService.getTestBySection(sectionId);

        int totalScore = 0;
        int maxPossibleScore = 0;
        int correctAnswers = 0;

        for(SectionsTests test : tests) {
            String paramName = "answer_" + test.getId();
            maxPossibleScore += test.getMaxScore();

            if(!allRequestParams.containsKey(paramName)) { continue;}

            List<String> answers = allRequestParams.get(paramName);
            Map<String, Object> answerData = new HashMap<>();
            if (answers.size() == 1) {
                answerData.put("value", answers.get(0));
            } else {
                answerData.put("values", answers);
            }

            // Проверяем, есть ли уже ответ от этого пользователя
            UserTestAnswer answer = userTestAnswerService.findByUserIdAndTests(user.getId(), test)
                    .orElse(new UserTestAnswer());

            // Обновляем или создаем новый ответ
            answer.setUser(user);
            answer.setTests(test);
            answer.setAnswerData(answerData);
            
            boolean isCorrect = userTestAnswerService.checkAnswer(test, answerData, answer);
            answer.setCorrect(isCorrect);
            answer.setScore(isCorrect ? test.getMaxScore() : 0);

            userTestAnswerService.saveAnswer(answer);

            if (isCorrect) {
                correctAnswers++;
                totalScore += test.getMaxScore();
            }
        }

        // Добавляем результаты в атрибуты для редиректа
        redirectAttributes.addFlashAttribute("testResults", true);
        redirectAttributes.addFlashAttribute("totalScore", totalScore);
        redirectAttributes.addFlashAttribute("maxPossibleScore", maxPossibleScore);
        redirectAttributes.addFlashAttribute("correctAnswers", correctAnswers);
        redirectAttributes.addFlashAttribute("totalQuestions", tests.size());

        return "redirect:/courses/" + courseId + "/sections";
    }
}
