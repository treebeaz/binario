package com.binario.controller;

import com.binario.entity.Course;
import com.binario.entity.Chapter;
import com.binario.entity.CourseSection;
import com.binario.entity.User;
import com.binario.repository.ChapterRepository;
import com.binario.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/teacher/courses")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherCourseController {

    private final CourseService courseService;
    private final ChapterService chapterService;
    private final CourseSectionService courseSectionService;
    private final UserCourseService userCourseService;
    private final UserService userService;

    @Autowired
    public TeacherCourseController(CourseService courseService,
                                   ChapterService chapterService,
                                   CourseSectionService courseSectionService, UserCourseService userCourseService, UserService userService) {
        this.courseService = courseService;
        this.chapterService = chapterService;
        this.courseSectionService = courseSectionService;
        this.userCourseService = userCourseService;
        this.userService = userService;
    }

    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourse());
        return "teacher/courses/list";
    }

    @GetMapping("/{courseId}/chapters")
    public String listChapters(@PathVariable Long courseId, Model model) {
        Course course = courseService.getCourseById(courseId);
        model.addAttribute("course", course);
        model.addAttribute("chapters", chapterService.getChaptersByCourseId(courseId));
        return "teacher/courses/chapters";
    }

    @GetMapping("/{courseId}/chapters/new")
    public String newChapterForm(@PathVariable Long courseId, Model model) {
        model.addAttribute("courseId", courseId);
        model.addAttribute("chapter", new Chapter());
        return "teacher/courses/chapter-form";
    }

    @PostMapping("/{courseId}/chapters")
    public String createChapter(@PathVariable Long courseId,
                              @ModelAttribute Chapter chapter,
                              RedirectAttributes redirectAttributes) {
        Course course = courseService.getCourseById(courseId);
        chapter.setCourse(course);
        chapterService.saveChapter(chapter);
        redirectAttributes.addFlashAttribute("message", "Глава успешно создана");
        return "redirect:/teacher/courses/" + courseId + "/chapters";
    }

    @GetMapping("/chapters/{chapterId}/sections")
    public String listSections(@PathVariable Long chapterId, Model model) {
        Chapter chapter = chapterService.getChapterById(chapterId);
        model.addAttribute("chapter", chapter);
        model.addAttribute("sections", courseSectionService.getAllSectionsByChapterId(chapter));
        return "teacher/courses/sections";
    }

    @GetMapping("/chapters/{chapterId}/sections/new")
    public String newSectionForm(@PathVariable Long chapterId, Model model) {
        model.addAttribute("chapterId", chapterId);
        model.addAttribute("section", new CourseSection());
        return "teacher/courses/section-form";
    }

    @PostMapping("/chapters/{chapterId}/sections")
    public String createSection(@PathVariable Long chapterId,
                              @ModelAttribute CourseSection section,
                              RedirectAttributes redirectAttributes) {
        Chapter chapter = chapterService.getChapterById(chapterId);
        section.setChapter(chapter);
        section.setCourse(chapter.getCourse());
        courseSectionService.saveSection(section);
        redirectAttributes.addFlashAttribute("message", "Раздел успешно создан");
        return "redirect:/teacher/courses/chapters/" + chapterId + "/sections";
    }

    @GetMapping("/sections/{sectionId}/edit")
    public String editSectionForm(@PathVariable Long sectionId, Model model) {
        CourseSection section = courseSectionService.getSectionById(sectionId);
        model.addAttribute("section", section);
        model.addAttribute("chapterId", section.getChapter().getId());

        return "teacher/courses/section-form";
    }

    @PostMapping("/sections/{sectionId}")
    public String updateSection(@PathVariable Long sectionId,
                                @ModelAttribute CourseSection section,
                                @RequestParam(value = "chapterId",required = false) Long chapterId,
                                RedirectAttributes redirectAttributes) {
        CourseSection existingSection = courseSectionService.getSectionById(sectionId);

        Chapter chapter = chapterService.getChapterById(chapterId);
        existingSection.setId(sectionId);
        existingSection.setChapter(chapter);
        existingSection.setTitle(section.getTitle());
        existingSection.setDescription(section.getDescription());
        existingSection.setContent(section.getContent());
        existingSection.setCourse(chapter.getCourse());
        courseSectionService.saveSection(existingSection);

        redirectAttributes.addFlashAttribute("message", "Раздел успешно обновлен");
        return "redirect:/teacher/courses/chapters/" + existingSection.getChapter().getId() + "/sections";
    }

    @GetMapping("/{courseId}/students")
    public String listStudents(@PathVariable Long courseId, Model model) {
        Course course = courseService.getCourseById(courseId);
        List<User> students = userCourseService.getStudentsByCourseId(courseId);

        model.addAttribute("course", course);
        model.addAttribute("students", students);

        return "teacher/courses/students";
    }

    @GetMapping("/new")
    public String newCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "teacher/courses/course-form";
    }

    @PostMapping
    public String createCourse(@ModelAttribute Course course,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User teacher = userService.findByUsername(userDetails.getUsername());
        course.setCreatedBy(teacher);
        courseService.saveCourse(course);

        redirectAttributes.addFlashAttribute("message", "Курс успешно создан");
        return "redirect:/teacher/courses";
    }

    @PostMapping("/{courseId}/delete")
    public String deleteCourse(@PathVariable Long courseId,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        Course course = courseService.getCourseById(courseId);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User teacher = userService.findByUsername(userDetails.getUsername());

        if(!Objects.equals(teacher.getId(), course.getCreatedBy().getId())) {
            redirectAttributes.addFlashAttribute("error", "Вы не можете удалить этот курс");
            return "redirect:/teacher/courses";
        }

        try{
            courseService.deleteCourse(courseId);
            redirectAttributes.addFlashAttribute("message", "Курс успешно удален");
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении курса");
        }

        return "redirect:/teacher/courses";
    }
} 