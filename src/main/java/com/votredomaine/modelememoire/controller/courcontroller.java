package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.model.Quiz;
import com.votredomaine.modelememoire.service.Courseservice;
import com.votredomaine.modelememoire.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
@RequestMapping("/teacher")
public class courcontroller {
    
    @Autowired
    private Courseservice courseService;
    
    @Autowired
    private QuizService quizService;  // ⭐ Injecté
    
    /**
     * Affiche le tableau de bord de l'enseignant avec les statistiques
     * et la liste des quiz
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        String teacherName = (String) session.getAttribute("teacherName");
        String teacherEmail = (String) session.getAttribute("teacherEmail");
        
        if (teacherId == null) {
            return "redirect:/login";
        }
        
        // ========== RÉCUPÉRATION DES COURS ==========
        List<Course> courses = courseService.getCoursesByTeacherId(teacherId);
        long totalCourses = courseService.getTotalCoursesByTeacher(teacherId);
        
        // ========== RÉCUPÉRATION DES QUIZZES ==========
        List<Quiz> allQuizzes = quizService.getQuizzesByTeacher(teacherId);
        long totalQuizzes = allQuizzes.size();
        
        // ========== COMPTER LES QUIZZES PAR COURS ==========
        for (Course course : courses) {
            long quizCount = quizService.countQuizzesByCourse(course.getId());
            course.setQuizCount((int) quizCount);
        }
        
        // ========== DERNIERS QUIZZES (5 derniers) ==========
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
    
    /**
     * API: Récupère tous les quiz de l'enseignant (pour AJAX)
     */
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
    
    /**
     * API: Supprime un quiz
     */
    @DeleteMapping("/api/delete-quiz/{quizId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteQuiz(@PathVariable Long quizId, HttpSession session) {
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
    
    // ========== MÉTHODES EXISTANTES (GARDER) ==========
    
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
    
    @PostMapping("/delete-course/{id}")
    public String deleteCourse(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
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
    
    @GetMapping("/my-courses")
    @ResponseBody
    public ResponseEntity<List<Course>> getCourses(HttpSession session) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        if (teacherId == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(courseService.getCoursesByTeacherId(teacherId));
    }
}