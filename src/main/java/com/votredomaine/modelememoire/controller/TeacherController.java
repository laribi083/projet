package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.model.Quiz;
import com.votredomaine.modelememoire.model.Teacher;
import com.votredomaine.modelememoire.service.Courseservice;
import com.votredomaine.modelememoire.service.QuizService;
import com.votredomaine.modelememoire.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/teacher")
public class TeacherController {
    
    @Autowired
    private Courseservice courseService;
    
    @Autowired
    private QuizService quizService;
    
    @Autowired
    private TeacherService teacherService;
    
    // ========== DASHBOARD ==========
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        String teacherName = (String) session.getAttribute("teacherName");
        String teacherEmail = (String) session.getAttribute("teacherEmail");
        
        if (teacherId == null) {
            return "redirect:/login";
        }
        
        // Récupération des cours
        List<Course> courses = courseService.getCoursesByTeacherId(teacherId);
        long totalCourses = courseService.getTotalCoursesByTeacher(teacherId);
        
        // Récupération des quiz
        List<Quiz> allQuizzes = quizService.getQuizzesByTeacher(teacherId);
        long totalQuizzes = allQuizzes.size();
        
        // Compter les quiz par cours
        for (Course course : courses) {
            long quizCount = quizService.countQuizzesByCourse(course.getId());
            course.setQuizCount((int) quizCount);
        }
        
        // Derniers quiz (5 derniers)
        List<Quiz> recentQuizzes = allQuizzes.stream()
                .limit(5)
                .collect(java.util.stream.Collectors.toList());
        
        model.addAttribute("teacherName", teacherName);
        model.addAttribute("teacherEmail", teacherEmail);
        model.addAttribute("teacherId", teacherId);
        model.addAttribute("courses", courses);
        model.addAttribute("totalCourses", totalCourses);
        model.addAttribute("totalQuizzes", totalQuizzes);
        model.addAttribute("allQuizzes", allQuizzes);
        model.addAttribute("recentQuizzes", recentQuizzes);
        model.addAttribute("totalStudents", 0);
        
        return "htmlTeacher/dashbord";
    }
    
    // ========== API COURS ==========
    
    @GetMapping("/my-courses")
    @ResponseBody
    public ResponseEntity<List<Course>> getMyCourses(HttpSession session) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        if (teacherId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Course> courses = courseService.getCoursesByTeacherId(teacherId);
        
        // Ajouter le nombre de quiz pour chaque cours
        for (Course course : courses) {
            long quizCount = quizService.countQuizzesByCourse(course.getId());
            course.setTotalQuizzes((int) quizCount);
        }
        
        return ResponseEntity.ok(courses);
    }
    
    @PostMapping("/api/courses")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createCourseApi(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("niveau") String niveau,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            String teacherName = (String) session.getAttribute("teacherName");
            
            if (teacherId == null) {
                response.put("success", false);
                response.put("message", "Session expirée");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            Course course = new Course();
            course.setTitle(title);
            course.setDescription(description);
            course.setNiveau(extractNiveauLevel(niveau));
            course.setModule(niveau);
            course.setTeacherId(teacherId);
            course.setTeacherName(teacherName);
            course.setStatus("ACTIVE");
            course.setCreatedAt(LocalDateTime.now());
            course.setUpdatedAt(LocalDateTime.now());
            
            Course savedCourse = courseService.save(course);
            
            response.put("success", true);
            response.put("course", savedCourse);
            response.put("courseId", savedCourse.getId());
            response.put("message", "Cours créé avec succès");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/course-view/{courseId}")
    public String viewCourseDetails(@PathVariable Long courseId, Model model, HttpSession session) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        
        if (teacherId == null) {
            return "redirect:/login";
        }
        
        Course course = courseService.getCourseById(courseId);
        if (course == null || !course.getTeacherId().equals(teacherId)) {
            return "redirect:/teacher/dashboard";
        }
        
        List<Quiz> quizzes = quizService.getQuizzesByCourse(courseId);
        
        model.addAttribute("course", course);
        model.addAttribute("quizzes", quizzes);
        
        return "htmlTeacher/course-details";
    }
    
    @DeleteMapping("/delete-course/{courseId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteCourseApi(@PathVariable Long courseId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            
            if (teacherId == null) {
                response.put("success", false);
                response.put("message", "Session expirée");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            Course course = courseService.getCourseById(courseId);
            
            if (course == null) {
                response.put("success", false);
                response.put("message", "Cours non trouvé");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            if (!course.getTeacherId().equals(teacherId)) {
                response.put("success", false);
                response.put("message", "Accès non autorisé");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            courseService.deleteCourse(courseId);
            response.put("success", true);
            response.put("message", "Cours supprimé avec succès");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/create-quiz")
    public String showCreateQuizForm(@RequestParam Long courseId, 
                                      @RequestParam String courseModule, 
                                      @RequestParam String courseNiveau,
                                      Model model,
                                      HttpSession session) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        
        if (teacherId == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("courseId", courseId);
        model.addAttribute("courseModule", courseModule);
        model.addAttribute("courseNiveau", courseNiveau);
        
        return "htmlTeacher/create-quiz";
    }
    
    // ========== API QUIZZES ==========
    
    @GetMapping("/api/quizzes")
    @ResponseBody
    public ResponseEntity<List<Quiz>> getTeacherQuizzes(HttpSession session) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        if (teacherId == null) {
            return ResponseEntity.badRequest().build();
        }
        List<Quiz> quizzes = quizService.getQuizzesByTeacher(teacherId);
        return ResponseEntity.ok(quizzes);
    }
    
    @DeleteMapping("/api/delete-quiz/{quizId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteQuizApi(@PathVariable Long quizId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            Quiz quiz = quizService.getQuizById(quizId);
            
            if (quiz == null) {
                response.put("success", false);
                response.put("message", "Quiz non trouvé");
                return ResponseEntity.ok(response);
            }
            
            if (!quiz.getTeacherId().equals(teacherId)) {
                response.put("success", false);
                response.put("message", "Accès non autorisé");
                return ResponseEntity.ok(response);
            }
            
            quizService.deleteQuiz(quizId);
            response.put("success", true);
            response.put("message", "Quiz supprimé avec succès");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    // ========== MÉTHODES UTILITAIRES ==========
    
    private String extractNiveauLevel(String moduleValue) {
        String[] firstYearModules = {
            "Algebra 01", "Algebra 02", "Analysis 01", "Analysis 02",
            "Advanced Data Structures 01", "Information Coding & Representation",
            "Component & Representation of Information", "Introduction to project-oriented programming",
            "Machine Structure", "Electricity & Electronics"
        };
        
        String[] secondYearModules = {
            "Advanced Data Structures 02", "Computer Architecture", "DataBases and SQL",
            "Software ingenery 01", "Logique mathématiques", "advanced project-oriented programming",
            "comunication network 01", "operating system", "langages theorique", "web development 01"
        };
        
        for (String m : firstYearModules) {
            if (m.equals(moduleValue)) return "1year";
        }
        
        for (String m : secondYearModules) {
            if (m.equals(moduleValue)) return "2year";
        }
        
        return "3year";
    }
    
    // ========== MÉTHODES EXISTANTES À GARDER ==========
    
    @GetMapping("/add-course")
    public String showAddCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "htmlTeacher/addcourse";
    }
    
    @PostMapping("/add-course")
    public String createCourse(@ModelAttribute Course course,
                               @RequestParam(value = "files", required = false) List<MultipartFile> files,
                               @RequestParam(value = "niveau", required = false) String niveau,
                               @RequestParam(value = "module", required = false) String module,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            String teacherName = (String) session.getAttribute("teacherName");
            
            if (teacherId == null) {
                redirectAttributes.addFlashAttribute("error", "Session expired");
                return "redirect:/login";
            }
            
            course.setTeacherId(teacherId);
            course.setTeacherName(teacherName);
            course.setNiveau(niveau);
            course.setModule(module);
            course.setStatus("ACTIVE");
            
            Course savedCourse = courseService.createCourse(course, files);
            redirectAttributes.addFlashAttribute("success", "Course created successfully!");
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        
        return "redirect:/teacher/dashboard";
    }
    
    @GetMapping("/edit-course/{id}")
    public String showEditCourseForm(@PathVariable Long id, Model model, HttpSession session) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        if (teacherId == null) return "redirect:/login";
        
        Course course = courseService.getCourseById(id);
        if (course == null || !course.getTeacherId().equals(teacherId)) {
            return "redirect:/teacher/dashboard";
        }
        
        model.addAttribute("course", course);
        return "htmlTeacher/editcourse";
    }
    
    @PostMapping("/update-course/{id}")
    public String updateCourse(@PathVariable Long id,
                               @ModelAttribute Course course,
                               @RequestParam(value = "newFiles", required = false) List<MultipartFile> newFiles,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            if (teacherId == null) {
                redirectAttributes.addFlashAttribute("error", "Session expired");
                return "redirect:/login";
            }
            
            courseService.updateCourse(id, course, newFiles);
            redirectAttributes.addFlashAttribute("success", "Course updated!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        
        return "redirect:/teacher/dashboard";
    }
    
    @PostMapping("/delete-course-form/{id}")
    public String deleteCourseForm(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        if (teacherId == null) {
            redirectAttributes.addFlashAttribute("error", "Session expired");
            return "redirect:/login";
        }
        
        Course course = courseService.getCourseById(id);
        if (course != null && course.getTeacherId().equals(teacherId)) {
            courseService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("success", "Course deleted!");
        }
        
        return "redirect:/teacher/dashboard";
    }
}