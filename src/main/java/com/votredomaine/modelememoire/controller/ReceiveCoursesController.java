package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.service.Courseservice;
import com.votredomaine.modelememoire.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ReceiveCoursesController {

    @Autowired
    private Courseservice courseService;
    
    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping("/receive-courses")
    public String showReceiveCourses(
            @RequestParam(value = "module", required = false) String module,
            @RequestParam(value = "search", required = false) String search,
            Model model, 
            HttpSession session) {
        
        String userName = (String) session.getAttribute("userName");
        Long userId = (Long) session.getAttribute("userId");
        
        System.out.println("=== RECEIVE COURSES ===");
        System.out.println("Étudiant: " + userName + " (ID: " + userId + ")");
        
        if (userName == null || userId == null) {
            return "redirect:/login";
        }
        
        // Récupérer les IDs des cours déjà téléchargés par l'étudiant
        List<Long> downloadedCourseIds = enrollmentService.getDownloadedCourseIds(userId);
        System.out.println("Cours déjà téléchargés: " + downloadedCourseIds);
        
        // Récupérer TOUS les cours avec status PUBLISHED
        List<Course> allPublishedCourses = courseService.findByStatus("PUBLISHED");
        System.out.println("Tous les cours PUBLISHED: " + allPublishedCourses.size());
        
        // Filtrer par module si nécessaire
        List<Course> filteredCourses = allPublishedCourses;
        if (module != null && !module.isEmpty() && !module.equals("all")) {
            filteredCourses = allPublishedCourses.stream()
                .filter(c -> c.getModule() != null && c.getModule().equals(module))
                .collect(Collectors.toList());
            System.out.println("Après filtre module: " + filteredCourses.size());
        }
        
        // Filtrer par recherche si nécessaire
        if (search != null && !search.isEmpty()) {
            filteredCourses = filteredCourses.stream()
                .filter(c -> c.getTitle() != null && c.getTitle().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());
            System.out.println("Après recherche: " + filteredCourses.size());
        }
        
        // Enlever les cours déjà téléchargés
        List<Course> availableCourses = filteredCourses.stream()
            .filter(course -> !downloadedCourseIds.contains(course.getId()))
            .collect(Collectors.toList());
        
        System.out.println("Cours disponibles à afficher: " + availableCourses.size());
        
        model.addAttribute("courses", availableCourses);
        model.addAttribute("userName", userName);
        model.addAttribute("selectedModule", module != null ? module : "all");
        model.addAttribute("searchTerm", search != null ? search : "");
        
        return "htmlstudent/receive-courses";
    }
}