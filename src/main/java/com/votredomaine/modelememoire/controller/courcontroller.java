package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.service.Courseservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/teacher")
public class courcontroller {
    
    @Autowired
    private Courseservice courseService;
    
    /**
     * Affiche le tableau de bord de l'enseignant
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        String teacherName = (String) session.getAttribute("teacherName");
        String teacherEmail = (String) session.getAttribute("teacherEmail");
        
        if (teacherId == null) {
            return "redirect:/login";
        }
        
        List<Course> courses = courseService.getCoursesByTeacherId(teacherId);
        long totalCourses = courseService.getTotalCoursesByTeacher(teacherId);
        
        model.addAttribute("teacherName", teacherName);
        model.addAttribute("teacherEmail", teacherEmail);
        model.addAttribute("courses", courses);
        model.addAttribute("totalCourses", totalCourses);
        
        return "htmlTeacher/dashbord";
    }
    
    /**
     * Affiche le formulaire d'ajout de cours
     */
    @GetMapping("/add-course")
    public String showAddCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "htmlTeacher/addcourse";
    }
    
    /**
     * Crée un nouveau cours
     */
    @PostMapping("/add-course")
    public String createCourse(@ModelAttribute Course course,
                               @RequestParam(value = "files", required = false) List<MultipartFile> files,
                               @RequestParam(value = "niveau", required = false) String niveau,
                               @RequestParam(value = "module", required = false) String module,
                               @RequestParam(value = "totalHours", required = false) Integer totalHours,
                               @RequestParam(value = "totalVideos", required = false) Integer totalVideos,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            String teacherName = (String) session.getAttribute("teacherName");
            
            if (teacherId == null) {
                redirectAttributes.addFlashAttribute("error", "Session expired. Please login again.");
                return "redirect:/login";
            }
            
            course.setTeacherId(teacherId);
            course.setTeacherName(teacherName);
            course.setNiveau(niveau);
            course.setModule(module);
            course.setStatus("ACTIVE");
            
            Course savedCourse = courseService.createCourse(course, files);
            redirectAttributes.addFlashAttribute("success", "Course created successfully!");
            System.out.println("✅ Cours créé: " + savedCourse.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error creating course: " + e.getMessage());
        }
        
        return "redirect:/teacher/dashboard";
    }
    
    /**
     * Affiche le formulaire d'édition d'un cours
     */
    @GetMapping("/edit-course/{id}")
    public String showEditCourseForm(@PathVariable Long id, Model model, HttpSession session) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        
        if (teacherId == null) {
            return "redirect:/login";
        }
        
        Course course = courseService.getCourseById(id);
        
        if (course == null) {
            return "redirect:/teacher/dashboard?error=Course not found";
        }
        
        Long courseTeacherId = course.getTeacherId();
        if (courseTeacherId == null || !courseTeacherId.equals(teacherId)) {
            return "redirect:/teacher/dashboard?error=Access denied";
        }
        
        model.addAttribute("course", course);
        return "htmlTeacher/editcourse";
    }
    
    /**
     * Met à jour un cours existant
     */
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
            
            Course existingCourse = courseService.getCourseById(id);
            
            if (existingCourse == null) {
                redirectAttributes.addFlashAttribute("error", "Course not found");
                return "redirect:/teacher/dashboard";
            }
            
            Long courseTeacherId = existingCourse.getTeacherId();
            if (courseTeacherId == null || !courseTeacherId.equals(teacherId)) {
                redirectAttributes.addFlashAttribute("error", "Access denied");
                return "redirect:/teacher/dashboard";
            }
            
            // Mettre à jour les champs
            existingCourse.setTitle(course.getTitle());
            existingCourse.setDescription(course.getDescription());
            existingCourse.setModule(course.getModule());
            existingCourse.setNiveau(course.getNiveau());
            existingCourse.setUpdatedAt(LocalDateTime.now());
            
            courseService.updateCourse(id, existingCourse, newFiles);
            redirectAttributes.addFlashAttribute("success", "Course updated successfully!");
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error updating course: " + e.getMessage());
        }
        
        return "redirect:/teacher/dashboard";
    }
    
    
    
    /**
     * Récupère la liste des cours de l'enseignant connecté (API)
     */
    @GetMapping("/my-courses")
    @ResponseBody
    public ResponseEntity<List<Course>> getCourses(HttpSession session) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        
        if (teacherId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Course> courses = courseService.getCoursesByTeacherId(teacherId);
        return ResponseEntity.ok(courses);
    }
}