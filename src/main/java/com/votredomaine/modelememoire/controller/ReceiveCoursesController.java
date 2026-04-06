package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.service.Courseservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ReceiveCoursesController {
    
    @Autowired
    private Courseservice courseService;
    
    @GetMapping("/receive-courses")
    public String showReceiveCourses(HttpSession session, Model model) {
        String userName = (String) session.getAttribute("userName");
        String niveau = (String) session.getAttribute("niveau");
        
        if (userName == null) {
            return "redirect:/login";
        }
        
        List<Course> allCourses = courseService.findAll();
        List<Course> activeCourses = allCourses.stream()
            .filter(c -> "ACTIVE".equals(c.getStatus()))
            .collect(Collectors.toList());
        
        long totalCourses = activeCourses.size();
        long total1stYear = activeCourses.stream().filter(c -> "1year".equals(c.getNiveau())).count();
        long total2ndYear = activeCourses.stream().filter(c -> "2year".equals(c.getNiveau())).count();
        long total3rdYear = activeCourses.stream().filter(c -> "3year".equals(c.getNiveau())).count();
        
        List<String> modules = activeCourses.stream()
            .map(Course::getModule)
            .filter(module -> module != null && !module.isEmpty())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        
        model.addAttribute("userName", userName);
        model.addAttribute("niveau", niveau);
        model.addAttribute("totalCourses", totalCourses);
        model.addAttribute("total1stYear", total1stYear);
        model.addAttribute("total2ndYear", total2ndYear);
        model.addAttribute("total3rdYear", total3rdYear);
        model.addAttribute("modules", modules);
        
        return "htmlstudent/receive-courses";
    }
    
    @GetMapping("/receive-courses/api/all")
    @ResponseBody
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> allCourses = courseService.findAll();
        List<Course> activeCourses = allCourses.stream()
            .filter(c -> "ACTIVE".equals(c.getStatus()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(activeCourses);
    }
    
    @GetMapping("/receive-courses/api/modules")
    @ResponseBody
    public ResponseEntity<List<String>> getAllModules() {
        List<Course> courses = courseService.findAll();
        List<String> modules = courses.stream()
            .filter(c -> "ACTIVE".equals(c.getStatus()))
            .map(Course::getModule)
            .filter(module -> module != null && !module.isEmpty())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        return ResponseEntity.ok(modules);
    }
    
    // ⭐ ENDPOINT POUR TÉLÉCHARGER UN FICHIER ⭐
    @GetMapping("/course/{courseId}/download")
    public ResponseEntity<Resource> downloadCourse(@PathVariable Long courseId) {
        try {
            Course course = courseService.getCourseById(courseId);
            if (course == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Récupérer le premier fichier
            String filePath = course.getFirstFilePath();
            if (filePath == null || filePath.isEmpty()) {
                // Essayer avec filePath simple
                filePath = course.getFilePath();
            }
            
            if (filePath == null || filePath.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Path path = Paths.get(filePath);
            Resource resource = new UrlResource(path.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                String fileName = course.getFirstFileName();
                if (fileName == null) fileName = course.getFileName();
                if (fileName == null) fileName = "course_" + courseId;
                
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
            } else {
                System.err.println("Fichier non trouvé: " + filePath);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // ⭐ ENDPOINT POUR VOIR LE CONTENU EN LIGNE ⭐
    @GetMapping("/course/{courseId}/view")
    public String viewCourseOnline(@PathVariable Long courseId, Model model) {
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return "redirect:/receive-courses";
        }
        
        String content = "";
        try {
            if (course.getHtmlContent() != null && !course.getHtmlContent().isEmpty()) {
                content = course.getHtmlContent();
            } else if (course.getFirstFilePath() != null) {
                Path filePath = Paths.get(course.getFirstFilePath());
                if (java.nio.file.Files.exists(filePath)) {
                    String fileName = course.getFirstFileName();
                    if (fileName != null && fileName.endsWith(".pdf")) {
                        // Pour les PDF, rediriger vers le téléchargement
                        return "redirect:/course/" + courseId + "/download";
                    } else {
                        content = "<pre style='white-space: pre-wrap; font-family: monospace; background: #f4f4f4; padding: 15px; border-radius: 8px;'>" + 
                                  java.nio.file.Files.readString(filePath) + "</pre>";
                    }
                } else {
                    content = "<div class='error'>Fichier non trouvé</div>";
                }
            } else {
                content = "<div class='info'>Aucun contenu disponible pour ce cours.</div>";
            }
        } catch (Exception e) {
            content = "<div class='error'>Erreur de lecture: " + e.getMessage() + "</div>";
        }
        
        model.addAttribute("course", course);
        model.addAttribute("content", content);
        return "htmlstudent/course-viewer";
    }
}