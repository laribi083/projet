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
    
    /**
     * Page qui affiche tous les cours disponibles pour l'étudiant
     * ⭐ UTILISE LA SOLUTION 1 : UNE SEULE REQUÊTE POUR LES IDs TÉLÉCHARGÉS
     */
    @GetMapping("/receive-courses")
    public String receiveCourses(Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        String niveau = (String) session.getAttribute("niveau");
        Long userId = (Long) session.getAttribute("userId");
        
        System.out.println("🔍 [receiveCourses] userName: " + userName + ", niveau: " + niveau + ", userId: " + userId);
        
        if (userName == null) {
            return "redirect:/login";
        }
        
        // Récupérer tous les cours PUBLISHED
        List<Course> allCourses = courseService.getAllActiveCourses();
        
        // Filtrer par niveau si nécessaire
        if (niveau != null && !niveau.isEmpty() && !"all".equals(niveau)) {
            allCourses = courseService.getCoursesByNiveau(niveau);
        }
        
        // ⭐⭐⭐ SOLUTION 1 : UNE SEULE REQUÊTE POUR RÉCUPÉRER TOUS LES IDs DES COURS TÉLÉCHARGÉS ⭐⭐⭐
        List<Long> downloadedIds = enrollmentService.getDownloadedCourseIds(userId);
        
        System.out.println("========================================");
        System.out.println("📊 RÉCAPITULATIF:");
        System.out.println("   - Cours disponibles: " + allCourses.size());
        System.out.println("   - Cours déjà téléchargés: " + downloadedIds.size());
        System.out.println("   - IDs téléchargés: " + downloadedIds);
        System.out.println("========================================");
        
        model.addAttribute("courses", allCourses);
        model.addAttribute("downloadedIds", downloadedIds);
        model.addAttribute("totalCourses", allCourses.size());
        model.addAttribute("userName", userName);
        model.addAttribute("niveau", niveau);
        
        return "htmlstudent/receive-courses";
    }
    
    /**
     * Page de détail d'un cours
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
        
        // Vérifier que le cours est publié
        if (!"PUBLISHED".equals(course.getStatus())) {
            System.out.println("⚠️ Cours non publié, accès refusé: " + course.getStatus());
            return "redirect:/student/receive-courses";
        }
        
        // Enregistrer le téléchargement
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
    
    /**
     * API pour récupérer les cours par niveau (AJAX)
     */
    @GetMapping("/api/courses/{niveau}")
    @ResponseBody
    public ResponseEntity<List<Course>> getCoursesByNiveau(@PathVariable String niveau) {
        List<Course> courses = courseService.getCoursesByNiveau(niveau);
        return ResponseEntity.ok(courses);
    }
    
    /**
     * API pour récupérer tous les cours (AJAX)
     */
    @GetMapping("/api/courses")
    @ResponseBody
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllActiveCourses();
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
    
    /**
     * ⭐ API pour récupérer les IDs des cours déjà téléchargés (AJAX)
     * Utilise la Solution 1
     */
    @GetMapping("/api/downloaded-ids")
    @ResponseBody
    public ResponseEntity<List<Long>> getDownloadedCourseIds(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        List<Long> downloadedIds = enrollmentService.getDownloadedCourseIds(userId);
        return ResponseEntity.ok(downloadedIds);
    }
    
    /**
     * API pour vérifier si un étudiant a déjà téléchargé un cours spécifique (AJAX)
     */
    @GetMapping("/api/check-downloaded/{courseId}")
    @ResponseBody
    public ResponseEntity<Boolean> checkDownloaded(@PathVariable Long courseId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null || courseId == null) {
            return ResponseEntity.ok(false);
        }
        
        // Utilise la liste des IDs pour vérifier (optimisé)
        List<Long> downloadedIds = enrollmentService.getDownloadedCourseIds(userId);
        boolean hasDownloaded = downloadedIds.contains(courseId);
        
        return ResponseEntity.ok(hasDownloaded);
    }
}