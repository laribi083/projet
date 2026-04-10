package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Teacher;
import com.votredomaine.modelememoire.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/test")
@CrossOrigin("*")
public class TeacherTestController {
    
    @Autowired
    private TeacherService teacherService;
    
    @PostMapping("/create-teacher")
    public ResponseEntity<?> createTestTeacher() {
        try {
            Teacher teacher = teacherService.registerTeacher(
                "Professeur Test",
                "teacher@test.com",
                "teacher123",
                "Informatique",
                "0612345678"
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Teacher de test créé avec succès");
            response.put("email", teacher.getEmail());
            response.put("password", "teacher123");
            response.put("department", teacher.getDepartment());
            response.put("teacherId", teacher.getId());
            response.put("name", teacher.getName());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/create-teacher")
    public ResponseEntity<?> createTestTeacherGet() {
        try {
            Teacher teacher = teacherService.registerTeacher(
                "Professeur Test",
                "teacher@test.com",
                "teacher123",
                "Informatique",
                "0612345678"
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Teacher de test créé avec succès");
            response.put("email", teacher.getEmail());
            response.put("password", "teacher123");
            response.put("department", teacher.getDepartment());
            response.put("teacherId", teacher.getId());
            response.put("name", teacher.getName());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}