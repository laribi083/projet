package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Teacher;
import com.votredomaine.modelememoire.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.List;  // ⚠️ AJOUTEZ CET IMPORT
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin("*")
public class TeacherController {
    
    @Autowired
    private TeacherService teacherService;
    
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
            dashboard.put("courses", "Liste des cours à venir...");
            
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
}