package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Teacher;
import com.votredomaine.modelememoire.model.Utilisateur;
import com.votredomaine.modelememoire.repository.UserRepository;
import com.votredomaine.modelememoire.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class LoginController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TeacherService teacherService;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Utilisateur user) {
        
        System.out.println("🔐 Tentative de connexion pour: " + user.getEmail());
        
        // 1. Vérifier d'abord si c'est un TEACHER
        Optional<Teacher> teacherOpt = teacherService.findByEmail(user.getEmail());
        
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            System.out.println("📝 Teacher trouvé: " + teacher.getEmail());
            
            // Vérifier le mot de passe du teacher
            if (passwordEncoder.matches(user.getPassword(), teacher.getPassword())) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login Success");
                response.put("userId", teacher.getId());
                response.put("userName", teacher.getName());
                response.put("email", teacher.getEmail());
                response.put("role", "TEACHER");
                response.put("redirectUrl", "/teacher/dashboard");
                
                System.out.println("✅ Connexion TEACHER réussie pour: " + user.getEmail());
                return ResponseEntity.ok(response);
            } else {
                System.out.println("❌ Mot de passe incorrect pour teacher: " + user.getEmail());
                return ResponseEntity.status(401).body("Email or Password incorrect");
            }
        }
        
        // 2. Si ce n'est pas un teacher, vérifier dans la table users (étudiants)
        Optional<Utilisateur> userOpt = userRepository.findByEmail(user.getEmail());
        
        if (userOpt.isPresent()) {
            Utilisateur existingUser = userOpt.get();
            System.out.println("📝 Utilisateur (étudiant) trouvé: " + existingUser.getEmail());
            
            if (passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login Success");
                response.put("userId", existingUser.getId());
                response.put("userName", existingUser.getName());
                response.put("email", existingUser.getEmail());
                response.put("role", "STUDENT");
                response.put("redirectUrl", "/student/dashboard");
                
                System.out.println("✅ Connexion ÉTUDIANT réussie pour: " + user.getEmail());
                return ResponseEntity.ok(response);
            } else {
                System.out.println("❌ Mot de passe incorrect pour étudiant: " + user.getEmail());
                return ResponseEntity.status(401).body("Email or Password incorrect");
            }
        }
        
        System.out.println("❌ Utilisateur non trouvé dans les deux tables: " + user.getEmail());
        return ResponseEntity.status(401).body("Email or Password incorrect");
    }
}