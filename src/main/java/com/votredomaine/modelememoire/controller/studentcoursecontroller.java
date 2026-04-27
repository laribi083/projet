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
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
public class studentcoursecontroller {
    
    @Autowired
    private Courseservice courseService;
    
    @Autowired
    private EnrollmentService enrollmentService;
    
    // ========== MÉTHODE UTILITAIRE POUR GROUPER LES COURS PAR MODULE ==========
    
    /**
     * Groupe les cours par module pour l'affichage dans les templates
     */
    private Map<String, List<Course>> groupCoursesByModule(List<Course> courses) {
        if (courses == null || courses.isEmpty()) {
            return new HashMap<>();
        }
        
        return courses.stream()
            .filter(course -> course.getModule() != null && !course.getModule().isEmpty())
            .collect(Collectors.groupingBy(
                course -> course.getModule(),
                LinkedHashMap::new,
                Collectors.toList()
            ));
    }
    
    // ========== PAGE DASHBOARD PRINCIPAL ==========
    // ⚠️ SUPPRIMÉE - Conflit avec DashboardController
    // La méthode studentDashboard() est déjà dans DashboardController
    
    // ========== PAGES DES NIVEAUX ==========
    
    /**
     * Page de sélection des niveaux (My Courses)
     */
    @GetMapping("/niveux")
    public String showLevelSelection(Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        if (userName == null) {
            return "redirect:/login";
        }
        model.addAttribute("userName", userName);
        return "htmlstudent/niveux";
    }
    
    /**
     * Page des cours - 1ère année
     */
    @GetMapping("/interface1er")
    public String getFirstYearCourses(Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        if (userName == null) {
            return "redirect:/login";
        }
        
        List<Course> courses = courseService.getCoursesByNiveauAndStatus("1year", "PUBLISHED");
        System.out.println("📚 1ère année - Nombre de cours trouvés: " + (courses != null ? courses.size() : 0));
        
        Map<String, List<Course>> coursesByModule = groupCoursesByModule(courses);
        
        System.out.println("📦 Modules disponibles en 1ère année:");
        for (String module : coursesByModule.keySet()) {
            System.out.println("   - " + module + " (" + coursesByModule.get(module).size() + " cours)");
        }
        
        model.addAttribute("courses", courses);
        model.addAttribute("coursesByModule", coursesByModule);
        model.addAttribute("level", "1st Year Fundamental");
        model.addAttribute("levelCode", "1year");
        model.addAttribute("userName", userName);
        
        return "htmlstudent/1year/interface1er";
    }
    
    /**
     * Page des cours - 2ème année
     */
    @GetMapping("/interface2eme")
    public String getSecondYearCourses(Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        if (userName == null) {
            return "redirect:/login";
        }
        
        List<Course> courses = courseService.getCoursesByNiveauAndStatus("2year", "PUBLISHED");
        System.out.println("📚 2ème année - Nombre de cours trouvés: " + (courses != null ? courses.size() : 0));
        
        Map<String, List<Course>> coursesByModule = groupCoursesByModule(courses);
        
        System.out.println("📦 Modules disponibles en 2ème année:");
        for (String module : coursesByModule.keySet()) {
            System.out.println("   - " + module + " (" + coursesByModule.get(module).size() + " cours)");
        }
        
        model.addAttribute("courses", courses);
        model.addAttribute("coursesByModule", coursesByModule);
        model.addAttribute("level", "2nd Year Fundamental");
        model.addAttribute("levelCode", "2year");
        model.addAttribute("userName", userName);
        
        return "htmlstudent/2year/interface2eme";
    }
    
    /**
     * Page des cours - 3ème année
     */
    @GetMapping("/interface3eme")
    public String getThirdYearCourses(Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        if (userName == null) {
            return "redirect:/login";
        }
        
        List<Course> courses = courseService.getCoursesByNiveauAndStatus("3year", "PUBLISHED");
        System.out.println("📚 3ème année - Nombre de cours trouvés: " + (courses != null ? courses.size() : 0));
        
        Map<String, List<Course>> coursesByModule = groupCoursesByModule(courses);
        
        System.out.println("📦 Modules disponibles en 3ème année:");
        for (String module : coursesByModule.keySet()) {
            System.out.println("   - " + module + " (" + coursesByModule.get(module).size() + " cours)");
        }
        
        model.addAttribute("courses", courses);
        model.addAttribute("coursesByModule", coursesByModule);
        model.addAttribute("level", "3rd Year Fundamental");
        model.addAttribute("levelCode", "3year");
        model.addAttribute("userName", userName);
        
        return "htmlstudent/3year/interface3eme";
    }
    
    /**
     * Page des cours - Master (optionnel)
     */
    @GetMapping("/interfaceMaster")
    public String getMasterCourses(Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        if (userName == null) {
            return "redirect:/login";
        }
        
        List<Course> courses = courseService.getCoursesByNiveauAndStatus("master", "PUBLISHED");
        Map<String, List<Course>> coursesByModule = groupCoursesByModule(courses);
        
        model.addAttribute("courses", courses);
        model.addAttribute("coursesByModule", coursesByModule);
        model.addAttribute("level", "Master");
        model.addAttribute("levelCode", "master");
        model.addAttribute("userName", userName);
        
        return "htmlstudent/master/interfaceMaster";
    }
    
    // ========== PAGES DES COURS ==========
    
    /**
     * Page qui affiche tous les cours disponibles pour l'étudiant
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
        
        List<Course> allCourses = courseService.getAllActiveCourses();
        
        if (niveau != null && !niveau.isEmpty() && !"all".equals(niveau)) {
            allCourses = courseService.getCoursesByNiveauAndStatus(niveau, "PUBLISHED");
        }
        
        List<Long> downloadedIds = enrollmentService.getDownloadedCourseIds(userId);
        
        System.out.println("========================================");
        System.out.println("📊 RÉCAPITULATIF:");
        System.out.println("   - Cours disponibles: " + allCourses.size());
        System.out.println("   - Cours déjà téléchargés: " + downloadedIds.size());
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
            return "redirect:/login";
        }
        
        if (userId == null) {
            return "redirect:/login";
        }
        
        Course course = courseService.getCourseById(id);
        
        if (course == null) {
            System.out.println("❌ Cours non trouvé avec ID: " + id);
            return "redirect:/student/receive-courses";
        }
        
        if (!"PUBLISHED".equals(course.getStatus())) {
            System.out.println("⚠️ Cours non publié, accès refusé: " + course.getStatus());
            return "redirect:/student/receive-courses";
        }
        
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
    
    /**
     * Téléchargement d'un fichier de cours
     */
    @GetMapping("/course/{id}/download")
    @ResponseBody
    public ResponseEntity<byte[]> downloadCourseFile(@PathVariable Long id) {
        return courseService.downloadCourseFile(id);
    }
    
    // ========== API REST POUR AJAX ==========
    
    @GetMapping("/api/courses/{niveau}")
    @ResponseBody
    public ResponseEntity<List<Course>> getCoursesByNiveau(@PathVariable String niveau) {
        List<Course> courses = courseService.getCoursesByNiveauAndStatus(niveau, "PUBLISHED");
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
    
    @GetMapping("/api/downloaded-ids")
    @ResponseBody
    public ResponseEntity<List<Long>> getDownloadedCourseIds(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        List<Long> downloadedIds = enrollmentService.getDownloadedCourseIds(userId);
        return ResponseEntity.ok(downloadedIds);
    }
    
    @GetMapping("/api/check-downloaded/{courseId}")
    @ResponseBody
    public ResponseEntity<Boolean> checkDownloaded(@PathVariable Long courseId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null || courseId == null) {
            return ResponseEntity.ok(false);
        }
        
        List<Long> downloadedIds = enrollmentService.getDownloadedCourseIds(userId);
        boolean hasDownloaded = downloadedIds.contains(courseId);
        
        return ResponseEntity.ok(hasDownloaded);
    }
}
