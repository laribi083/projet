package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.model.Rating;
import com.votredomaine.modelememoire.service.Courseservice;
import com.votredomaine.modelememoire.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RatingController {
    
    @Autowired
    private RatingService ratingService;
    
    @Autowired
    private Courseservice courseService;
    
    // ==================== PAGES HTML ====================
    
    @GetMapping("/student/ratings")
    public String studentRatings(Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        Long userId = (Long) session.getAttribute("userId");
        
        if (userName == null || userId == null) {
            return "redirect:/login";
        }
        
        List<Rating> myRatings = ratingService.getRatingsByStudent(userId);
        
        model.addAttribute("userName", userName);
        model.addAttribute("myRatings", myRatings);
        model.addAttribute("totalRatings", myRatings.size());
        
        return "htmlstudent/ratings";
    }
    
    @GetMapping("/teacher/ratings")
    public String teacherRatings(Model model, HttpSession session) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        String teacherName = (String) session.getAttribute("teacherName");
        
        if (teacherId == null) {
            return "redirect:/login";
        }
        
        List<Rating> courseRatings = ratingService.getRatingsByTeacher(teacherId);
        
        model.addAttribute("teacherName", teacherName);
        model.addAttribute("ratings", courseRatings);
        model.addAttribute("totalRatings", courseRatings.size());
        
        return "htmlTeacher/ratings";
    }
    
    // ==================== API REST ====================
    
    @PostMapping("/api/ratings/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addRating(
            @RequestParam Long courseId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = (Long) session.getAttribute("userId");
            String userName = (String) session.getAttribute("userName");
            
            System.out.println("=== ADD RATING ===");
            System.out.println("userId: " + userId);
            System.out.println("userName: " + userName);
            System.out.println("courseId: " + courseId);
            System.out.println("rating: " + rating);
            System.out.println("comment: " + comment);
            
            if (userId == null) {
                response.put("success", false);
                response.put("message", "Veuillez vous connecter");
                return ResponseEntity.status(401).body(response);
            }
            
            if (rating < 1 || rating > 5) {
                response.put("success", false);
                response.put("message", "La note doit être entre 1 et 5");
                return ResponseEntity.badRequest().body(response);
            }
            
            Rating savedRating = ratingService.saveOrUpdateRating(userId, userName, courseId, rating, comment);
            
            response.put("success", true);
            response.put("message", "Votre note a été enregistrée !");
            response.put("ratingId", savedRating.getId());
            response.put("average", ratingService.getAverageRatingForCourse(courseId));
            response.put("count", ratingService.getRatingCountForCourse(courseId));
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/api/ratings/course/{courseId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCourseRatings(@PathVariable Long courseId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Rating> ratings = ratingService.getRatingsByCourse(courseId);
            Map<String, Object> stats = ratingService.getRatingStatsForCourse(courseId);
            
            Long userId = (Long) session.getAttribute("userId");
            boolean hasRated = false;
            Integer userRating = null;
            
            if (userId != null) {
                hasRated = ratingService.hasRated(userId, courseId);
                var userRatingOpt = ratingService.getRatingByStudentAndCourse(userId, courseId);
                if (userRatingOpt.isPresent()) {
                    userRating = userRatingOpt.get().getRatingValue();
                }
            }
            
            response.put("success", true);
            response.put("ratings", ratings);
            response.put("average", stats.get("average"));
            response.put("totalCount", stats.get("count"));
            response.put("hasRated", hasRated);
            response.put("userRating", userRating);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/api/ratings/my-ratings")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMyRatings(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = (Long) session.getAttribute("userId");
            
            if (userId == null) {
                response.put("success", false);
                response.put("message", "Non connecté");
                return ResponseEntity.status(401).body(response);
            }
            
            List<Rating> myRatings = ratingService.getRatingsByStudent(userId);
            
            response.put("success", true);
            response.put("ratings", myRatings);
            response.put("count", myRatings.size());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/api/ratings/teacher/all")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTeacherRatings(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            
            System.out.println("=== API RÉCUPÉRATION NOTES ENSEIGNANT ===");
            System.out.println("Teacher ID: " + teacherId);
            
            if (teacherId == null) {
                response.put("success", false);
                response.put("message", "Non authentifié");
                return ResponseEntity.status(401).body(response);
            }
            
            List<Rating> ratings = ratingService.getRatingsByTeacher(teacherId);
            System.out.println("Nombre de notes trouvées: " + ratings.size());
            
            response.put("success", true);
            response.put("ratings", ratings);
            response.put("count", ratings.size());
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/api/ratings/{ratingId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteRating(@PathVariable Long ratingId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = (Long) session.getAttribute("userId");
            
            if (userId == null) {
                response.put("success", false);
                response.put("message", "Non connecté");
                return ResponseEntity.status(401).body(response);
            }
            
            ratingService.deleteRating(ratingId, userId);
            
            response.put("success", true);
            response.put("message", "Note supprimée avec succès");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    @GetMapping("/test-rating-page")
@ResponseBody
public String testPage() {
    return "RatingController fonctionne - La page /teacher/ratings devrait être accessible";
}
}