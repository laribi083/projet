package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Utilisateur;
import com.votredomaine.modelememoire.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;

@RestController
@RequestMapping("/api/inscription")
@CrossOrigin("*")
public class InscriptionController {
    
    @Autowired
    private UserRepository userRepository;
    
    // Ajoutez cet encodeur
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping
    public ResponseEntity<String> inscription(@RequestBody Utilisateur utilisateur) {
       
        Optional<Utilisateur> existingUser = userRepository.findByEmail(utilisateur.getEmail());
        
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        
        
        String hashedPassword = passwordEncoder.encode(utilisateur.getPassword());
        utilisateur.setPassword(hashedPassword);
    
        Utilisateur savedUser = userRepository.save(utilisateur);
        
      
        System.out.println("✅ UTILISATEUR CRÉÉ: " + savedUser.getEmail() + " avec ID: " + savedUser.getId());
        
        return ResponseEntity.ok("Account created successfully");
    }
}