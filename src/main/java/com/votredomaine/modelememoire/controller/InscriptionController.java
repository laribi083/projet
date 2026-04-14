package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Utilisateur;
import com.votredomaine.modelememoire.repository.UserRepository;
import com.votredomaine.modelememoire.service.ActivityService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/inscription")
public class InscriptionController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ActivityService activityService;  // ⭐ AJOUTER CETTE LIGNE
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping
    public ResponseEntity<Map<String, Object>> inscription(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String name = request.get("name");
            String email = request.get("email");
            String password = request.get("password");
            
            System.out.println("📝 Tentative d'inscription pour: " + email);
            
            // Validation des champs
            if (name == null || name.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Le nom est requis");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "L'email est requis");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (password == null || password.length() < 6) {
                response.put("success", false);
                response.put("message", "Le mot de passe doit contenir au moins 6 caractères");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Vérifier si l'email existe déjà
            Optional<Utilisateur> existingUser = userRepository.findByEmail(email);
            
            if (existingUser.isPresent()) {
                response.put("success", false);
                response.put("message", "Cet email est déjà utilisé");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Créer le nouvel utilisateur
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setName(name);
            utilisateur.setEmail(email);
            
            // Hasher le mot de passe
            String hashedPassword = passwordEncoder.encode(password);
            utilisateur.setPassword(hashedPassword);
            
            // Sauvegarder
            Utilisateur savedUser = userRepository.save(utilisateur);
            
            System.out.println("✅ UTILISATEUR CRÉÉ: " + savedUser.getEmail() + " avec ID: " + savedUser.getId());
            
            // ⭐ AJOUTER L'ACTIVITÉ DANS LA TABLE ACTIVITIES ⭐
            activityService.logUserRegistered(name, "STUDENT");
            
            response.put("success", true);
            response.put("message", "Inscription réussie !");
            response.put("userId", savedUser.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Erreur interne: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}