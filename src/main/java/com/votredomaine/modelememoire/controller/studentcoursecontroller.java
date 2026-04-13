package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.service.Courseservice;
import com.votredomaine.modelememoire.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/student")
public class studentcoursecontroller {
    
    @Autowired
    private Courseservice courseService;
    
    @Autowired
    private EnrollmentService enrollmentService;
    
    // ========== PAGES HTML ==========
    
    @GetMapping("/receive-courses")
    public String receiveCourses(Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        String niveau = (String) session.getAttribute("niveau");
        
        System.out.println("🔍 [receiveCourses] userName: " + userName);
        
        if (userName == null) {
            return "redirect:/login";
        }
        
        List<Course> allCourses = courseService.getAllActiveCourses();
        
        model.addAttribute("courses", allCourses);
        model.addAttribute("totalCourses", allCourses.size());
        model.addAttribute("userName", userName);
        model.addAttribute("niveau", niveau);
        
        return "htmlstudent/receive-courses";
    }
    
    /**
     * PAGE DÉTAIL D'UN COURS - Avec enregistrement du téléchargement
     */
    @GetMapping("/course/{id}")
    public String viewCourse(@PathVariable Long id, Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        Long userId = (Long) session.getAttribute("userId");
        
        System.out.println("========================================");
        System.out.println("🔍 [viewCourse] APPELÉ");
        System.out.println("   courseId: " + id);
        System.out.println("   userName: " + userName);
        System.out.println("   userId: " + userId);
        System.out.println("========================================");
        
        if (userName == null) {
            System.out.println("⚠️ userName null, redirection login");
            return "redirect:/login";
        }
        
        if (userId == null) {
            System.out.println("⚠️ userId null, redirection login");
            return "redirect:/login";
        }
        
        Course course = courseService.getCourseById(id);
        
        if (course == null) {
            System.out.println("❌ Cours non trouvé avec ID: " + id);
            return "redirect:/student/receive-courses";
        }
        
        // ⭐⭐⭐ POINT CRITIQUE : Enregistrer le téléchargement ⭐⭐⭐
        System.out.println("📝 Appel de enrollmentService.registerDownload()...");
        try {
            boolean isNew = enrollmentService.registerDownload(userId, userName, id);
            System.out.println("📥 Résultat registerDownload: " + (isNew ? "NOUVELLE inscription" : "Déjà inscrit"));
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'enregistrement: " + e.getMessage());
            e.printStackTrace();
        }
        
        model.addAttribute("course", course);
        model.addAttribute("userName", userName);
        
        return "htmlstudent/course-detail";
    }
    
    // ========== API REST POUR AJAX ==========
    
    @GetMapping("/api/courses/{niveau}")
    @ResponseBody
    public ResponseEntity<List<Course>> getCoursesByNiveau(@PathVariable String niveau) {
        List<Course> courses = courseService.getCoursesByNiveau(niveau);
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping("/api/courses")
    @ResponseBody
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllActiveCourses();
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping("/api/courses/all")
    @ResponseBody
    public ResponseEntity<List<Course>> getAllCoursesAlias() {
        return getAllCourses();
    }
}