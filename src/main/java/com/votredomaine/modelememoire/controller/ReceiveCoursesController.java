package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.service.Courseservice;
import com.votredomaine.modelememoire.service.EnrollmentService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ReceiveCoursesController {
    
    @Autowired
    private Courseservice courseService;
    
    @Autowired
    private EnrollmentService enrollmentService;  // ⭐ AJOUTER CETTE LIGNE
    
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
    
    // ⭐ ENDPOINT POUR TÉLÉCHARGER UN COURS (AVEC ENREGISTREMENT DANS ENROLLMENTS)
    @GetMapping("/course/{courseId}/download")
    public ResponseEntity<Resource> downloadCourse(@PathVariable Long courseId, HttpSession session) {
        try {
            String userName = (String) session.getAttribute("userName");
            Long userId = (Long) session.getAttribute("userId");
            
            System.out.println("========================================");
            System.out.println("📥 [DOWNLOAD] Tentative de téléchargement");
            System.out.println("   courseId: " + courseId);
            System.out.println("   userName: " + userName);
            System.out.println("   userId: " + userId);
            System.out.println("========================================");
            
            if (userName == null || userId == null) {
                System.err.println("❌ Utilisateur non connecté");
                return ResponseEntity.status(401).build();
            }
            
            Course course = courseService.getCourseById(courseId);
            
            if (course == null) {
                System.err.println("❌ Cours non trouvé");
                return ResponseEntity.notFound().build();
            }
            
            // ⭐⭐⭐ ENREGISTRER L'INSCRIPTION DANS LA TABLE ENROLLMENTS ⭐⭐⭐
            try {
                boolean isNew = enrollmentService.registerDownload(userId, userName, courseId);
                System.out.println("📥 Inscription enregistrée - Nouvelle: " + isNew);
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de l'enregistrement: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Mettre à jour le compteur de téléchargements du cours
            course.incrementDownloadCount();
            courseService.update(course);
            
            System.out.println("📥 Téléchargement du cours: " + course.getTitle() + " par " + userName + 
                               " (Téléchargé " + course.getDownloadCount() + " fois)");
            
            String filePath = course.getFirstFilePath();
            if (filePath == null || filePath.isEmpty()) {
                System.err.println("❌ Aucun fichier associé au cours");
                return ResponseEntity.notFound().build();
            }
            
            Path path = Paths.get(filePath);
            Resource resource = new UrlResource(path.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                String fileName = course.getFirstFileName();
                if (fileName == null) fileName = "course_" + courseId;
                
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
            } else {
                System.err.println("❌ Fichier non trouvé: " + filePath);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // ⭐ ENDPOINT POUR RÉCUPÉRER LES COURS RÉCEMMENT TÉLÉCHARGÉS
    @GetMapping("/api/recent-downloads")
    @ResponseBody
    public ResponseEntity<List<Course>> getRecentDownloads(HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        List<Course> allCourses = courseService.findAll();
        
        List<Course> recentDownloads = allCourses.stream()
            .filter(c -> c.getLastDownloadedAt() != null)
            .sorted((a, b) -> b.getLastDownloadedAt().compareTo(a.getLastDownloadedAt()))
            .limit(5)
            .collect(Collectors.toList());
        
        System.out.println("📚 " + recentDownloads.size() + " cours récemment téléchargés pour " + userName);
        return ResponseEntity.ok(recentDownloads);
    }
    
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
                        return "redirect:/course/" + courseId + "/download";
                    } else {
                        content = "<pre style='white-space: pre-wrap; font-family: monospace; background: #f4f4f4; padding: 15px; border-radius: 8px;'>" + 
                                  java.nio.file.Files.readString(filePath) + "</pre>";
                    }
                } else {
                    content = "<div class='error-message'>Fichier non trouvé</div>";
                }
            } else {
                content = "<div class='info-message'>Aucun contenu disponible pour ce cours.</div>";
            }
        } catch (Exception e) {
            content = "<div class='error-message'>Erreur de lecture: " + e.getMessage() + "</div>";
        }
        
        model.addAttribute("course", course);
        model.addAttribute("content", content);
        return "htmlstudent/course-viewer";
    }
}