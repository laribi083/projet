package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.model.Teacher;
import com.votredomaine.modelememoire.service.Courseservice;
import com.votredomaine.modelememoire.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/teacher")
@CrossOrigin("*")
public class TeacherController {
    
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private Courseservice courseService;
    
    // ========== GESTION DES ENSEIGNANTS ==========
    
    @PostMapping("/register")
    public ResponseEntity<?> registerTeacher(@RequestBody Map<String, String> request) {
        try {
            Teacher teacher = teacherService.registerTeacher(
                request.get("name"),
                request.get("email"),
                request.get("password"),
                request.get("department"),
                request.get("phone")
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Teacher registered successfully");
            response.put("teacherId", teacher.getId());
            response.put("name", teacher.getName());
            response.put("email", teacher.getEmail());
            response.put("department", teacher.getDepartment());
            response.put("phone", teacher.getPhone());
            
            System.out.println("✅ NOUVEAU TEACHER CRÉÉ: " + teacher.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/dashboard/{email}")
    public ResponseEntity<?> getTeacherDashboard(@PathVariable String email) {
        Optional<Teacher> teacherOpt = teacherService.findByEmail(email);
        
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("teacher", Map.of(
                "id", teacher.getId(),
                "name", teacher.getName(),
                "email", teacher.getEmail(),
                "department", teacher.getDepartment(),
                "phone", teacher.getPhone()
            ));
            dashboard.put("message", "Bienvenue dans l'interface Teacher!");
            
            List<Course> courses = courseService.findByTeacherId(teacher.getId());
            dashboard.put("courses", courses);
            
            return ResponseEntity.ok(dashboard);
        }
        
        return ResponseEntity.status(404).body("Teacher not found");
    }
    
    @GetMapping("/profile/{email}")
    public ResponseEntity<?> getTeacherProfile(@PathVariable String email) {
        Optional<Teacher> teacherOpt = teacherService.findByEmail(email);
        
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", teacher.getId());
            profile.put("name", teacher.getName());
            profile.put("email", teacher.getEmail());
            profile.put("department", teacher.getDepartment());
            profile.put("phone", teacher.getPhone());
            profile.put("createdAt", teacher.getCreatedAt());
            
            return ResponseEntity.ok(profile);
        }
        
        return ResponseEntity.status(404).body("Teacher not found");
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllTeachers() {
        try {
            List<Teacher> teachers = teacherService.getAllTeachers();
            
            if (teachers.isEmpty()) {
                return ResponseEntity.ok(Map.of("message", "Aucun enseignant trouvé", "count", 0));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("count", teachers.size());
            response.put("teachers", teachers);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    // ========== GESTION DES COURS ==========
    
    /**
     * Créer un nouveau cours
     * Endpoint: POST /teacher/api/courses
     */
    @PostMapping("/api/courses")
    public ResponseEntity<Map<String, Object>> createCourse(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("niveau") String niveau,
            @RequestParam(value = "files", required = false) MultipartFile[] files) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Course course = new Course();
            course.setTitle(title);
            course.setDescription(description);
            course.setNiveau(extractNiveauLevel(niveau));
            course.setModule(niveau);
            course.setStatus("ACTIVE");
            course.setCreatedAt(LocalDateTime.now());
            course.setUpdatedAt(LocalDateTime.now());
            
            Course savedCourse = courseService.save(course);
            
            response.put("success", true);
            response.put("course", savedCourse);
            response.put("message", "Cours créé avec succès");
            
            System.out.println("✅ COURS CRÉÉ: " + title + " pour le module " + niveau);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Récupérer tous les cours - ⭐ CHANGER LE CHEMIN POUR ÉVITER LE CONFLIT
     * Endpoint: GET /teacher/my-courses
     */
    @GetMapping("/my-courses")
    public ResponseEntity<List<Course>> getTeacherCourses(
            @RequestParam(value = "teacherId", required = false) Long teacherId) {
        
        List<Course> courses;
        
        if (teacherId != null) {
            courses = courseService.findByTeacherId(teacherId);
        } else {
            courses = courseService.findAll();
        }
        
        return ResponseEntity.ok(courses);
    }
    
    /**
     * Supprimer un cours
     * Endpoint: DELETE /teacher/delete-course/{courseId}
     */
    @DeleteMapping("/delete-course/{courseId}")
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable Long courseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            courseService.deleteCourse(courseId);
            response.put("success", true);
            response.put("message", "Cours supprimé avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Récupérer un cours par son ID
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long courseId) {
        Course course = courseService.getCourseById(courseId);
        if (course != null) {
            return ResponseEntity.ok(course);
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Récupérer les cours par niveau
     */
    @GetMapping("/courses/niveau/{niveau}")
    public ResponseEntity<List<Course>> getCoursesByNiveau(@PathVariable String niveau) {
        List<Course> courses = courseService.findByNiveau(niveau);
        return ResponseEntity.ok(courses);
    }
    
    // ========== MÉTHODES UTILITAIRES ==========
    
    private String extractNiveauLevel(String moduleValue) {
        String[] firstYearModules = {
            "Algebra 01", "Algebra 02", "Analysis 01", "Analysis 02",
            "Advanced Data Structures 01", "Information Coding & Representation",
            "Component & Representation of Information", "Introduction to  project-oriented programming",
            "Machine Structure", "Electricity & Electronics"
        };
        
        String[] secondYearModules = {
            "Advanced Data Structures 02", "Computer Architecture", "DataBases and SQL",
            "Softwere ingenery 01", "Logique mathématiques", "advanced project-oriented programming",
            "comunication network 01", "operating system", "langages theorique", "web development 01"
        };
        
        for (String m : firstYearModules) {
            if (m.equals(moduleValue)) return "1year";
        }
        
        for (String m : secondYearModules) {
            if (m.equals(moduleValue)) return "2year";
        }
        
        return "3year";
    }
}