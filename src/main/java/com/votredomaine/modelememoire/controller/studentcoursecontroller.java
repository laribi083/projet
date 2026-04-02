package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.service.Courseservice;
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
    
    // ========== PAGES HTML ==========
    
    /**
     * ⭐ PAGE RECEIVE COURSES - Affiche tous les cours disponibles
     */
    @GetMapping("/receive-courses")
    public String receiveCourses(Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        String niveau = (String) session.getAttribute("niveau");
        
        if (userName == null) {
            return "redirect:/login";
        }
        
        // Récupérer tous les cours actifs
        List<Course> allCourses = courseService.getAllActiveCourses();
        
        System.out.println("📚 [ReceiveCourses] Affichage de " + allCourses.size() + " cours");
        
        model.addAttribute("courses", allCourses);
        model.addAttribute("totalCourses", allCourses.size());
        model.addAttribute("userName", userName);
        model.addAttribute("niveau", niveau);
        
        return "htmlstudent/receive-courses";
    }
    
    /**
     * ⭐ PAGE DÉTAIL D'UN COURS
     */
    @GetMapping("/course/{id}")
    public String viewCourse(@PathVariable Long id, Model model, HttpSession session) {
        Course course = courseService.getCourseById(id);
        
        if (course == null) {
            return "redirect:/student/receive-courses";
        }
        
        model.addAttribute("course", course);
        return "htmlstudent/course-detail";
    }
    
    // ========== API REST POUR AJAX ==========
    
    /**
     * ⭐ API pour récupérer les cours par niveau (utilisée par AJAX)
     */
    @GetMapping("/api/courses/{niveau}")
    @ResponseBody
    public ResponseEntity<List<Course>> getCoursesByNiveau(@PathVariable String niveau) {
        List<Course> courses = courseService.getCoursesByNiveau(niveau);
        System.out.println("📚 API /student/api/courses/" + niveau + " - " + courses.size() + " cours");
        return ResponseEntity.ok(courses);
    }
    
    /**
     * ⭐ API pour récupérer TOUS les cours actifs (utilisée par receive-courses.js)
     */
    @GetMapping("/api/courses")
    @ResponseBody
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllActiveCourses();
        System.out.println("📚 API /student/api/courses - " + courses.size() + " cours");
        return ResponseEntity.ok(courses);
    }
    
    /**
     * API pour récupérer tous les cours (alias)
     */
    @GetMapping("/api/courses/all")
    @ResponseBody
    public ResponseEntity<List<Course>> getAllCoursesAlias() {
        return getAllCourses();
    }
}