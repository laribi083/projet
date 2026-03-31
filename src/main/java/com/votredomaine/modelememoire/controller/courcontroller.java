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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/teacher")
public class courcontroller {
    
    @Autowired
    private Courseservice courseService;
    
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
    
    @GetMapping("/edit-course/{id}")
    public String showEditCourseForm(@PathVariable Long id, Model model, HttpSession session) {
        Course course = courseService.getCourseById(id);
        Long teacherId = (Long) session.getAttribute("teacherId");
        
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
            Course existingCourse = courseService.getCourseById(id);
            
            if (existingCourse == null || !existingCourse.getTeacherId().equals(teacherId)) {
                redirectAttributes.addFlashAttribute("error", "Course not found");
                return "redirect:/teacher/dashboard";
            }
            
            courseService.updateCourse(id, course, newFiles);
            redirectAttributes.addFlashAttribute("success", "Course updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error updating course: " + e.getMessage());
        }
        
        return "redirect:/teacher/dashboard";
    }
    
    @DeleteMapping("/delete-course/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            Course course = courseService.getCourseById(id);
            
            if (course == null || !course.getTeacherId().equals(teacherId)) {
                response.put("success", false);
                response.put("message", "Course not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            courseService.deleteCourse(id);
            response.put("success", true);
            response.put("message", "Course deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ⭐ MODIFICATION ICI : Changer le chemin pour éviter le conflit
    @GetMapping("/teacher-courses")  // Changé de /api/courses à /teacher-courses
    @ResponseBody
    public ResponseEntity<List<Course>> getCourses(HttpSession session) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        List<Course> courses = courseService.getCoursesByTeacherId(teacherId);
        return ResponseEntity.ok(courses);
    }
}