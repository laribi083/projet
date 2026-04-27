package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.service.Courseservice;
import com.votredomaine.modelememoire.service.EnrollmentService;
import com.votredomaine.modelememoire.service.RatingService;
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
    
    @Autowired
    private RatingService ratingService;

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
        
        // Récupérer les IDs des cours déjà téléchargés
        List<Long> downloadedCourseIds = enrollmentService.getDownloadedCourseIds(userId);
        System.out.println("Cours déjà téléchargés: " + downloadedCourseIds);
        
        // Récupérer TOUS les cours PUBLISHED
        List<Course> allPublishedCourses = courseService.findByStatus("PUBLISHED");
        System.out.println("Tous les cours PUBLISHED: " + allPublishedCourses.size());
        
        // Filtrer par module
        List<Course> filteredCourses = allPublishedCourses;
        if (module != null && !module.isEmpty() && !module.equals("all")) {
            filteredCourses = allPublishedCourses.stream()
                .filter(c -> c.getModule() != null && c.getModule().equals(module))
                .collect(Collectors.toList());
            System.out.println("Après filtre module: " + filteredCourses.size());
        }
        
        // Filtrer par recherche
        if (search != null && !search.isEmpty()) {
            filteredCourses = filteredCourses.stream()
                .filter(c -> c.getTitle() != null && c.getTitle().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());
            System.out.println("Après recherche: " + filteredCourses.size());
        }
        
        // ⭐⭐⭐ CHARGER LES DONNÉES DE NOTATION POUR CHAQUE COURS ⭐⭐⭐
        for (Course course : filteredCourses) {
            try {
                // Moyenne des notes
                double avgRating = ratingService.getAverageRatingForCourse(course.getId());
                long ratingCount = ratingService.getRatingCountForCourse(course.getId());
                course.setAverageRating(avgRating);
                course.setRatingCount(ratingCount);
                
                // Vérifier si l'étudiant a déjà noté ce cours
                boolean hasRated = ratingService.hasRated(userId, course.getId());
                course.setUserHasRated(hasRated);
                
                if (hasRated) {
                    var userRating = ratingService.getRatingByStudentAndCourse(userId, course.getId());
                    userRating.ifPresent(r -> course.setUserRating(r.getRatingValue()));
                }
                
                System.out.println("Cours: " + course.getTitle() + 
                                   " | Moyenne: " + avgRating + 
                                   " | Notes: " + ratingCount + 
                                   " | A noté: " + hasRated +
                                   " | Note utilisateur: " + (hasRated ? course.getUserRating() : "N/A"));
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement des notes pour le cours " + course.getId() + ": " + e.getMessage());
                // Initialiser avec des valeurs par défaut
                course.setAverageRating(0.0);
                course.setRatingCount(0L);
                course.setUserHasRated(false);
                course.setUserRating(0);
            }
        }
        
        model.addAttribute("courses", filteredCourses);
        model.addAttribute("downloadedCourseIds", downloadedCourseIds);
        model.addAttribute("userName", userName);
        model.addAttribute("selectedModule", module != null ? module : "all");
        model.addAttribute("searchTerm", search != null ? search : "");
        
        System.out.println("Nombre de cours envoyés à la vue: " + filteredCourses.size());
        
        return "htmlstudent/receive-courses";
    }
}