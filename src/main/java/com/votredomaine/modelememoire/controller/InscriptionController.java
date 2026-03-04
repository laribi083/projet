package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Utilisateur;
import com.votredomaine.modelememoire.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;  // Ajouté pour meilleur contrôle des réponses
import java.util.Optional;  // Ajouté

@RestController
@RequestMapping("/api/inscription")
@CrossOrigin("*")
public class InscriptionController {
    private final UserRepository userRepository;

    public InscriptionController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<String> inscription(@RequestBody Utilisateur utilisateur) {
        // Vérifier si l'email existe déjà
        Optional<Utilisateur> existingUser = userRepository.findByEmail(utilisateur.getEmail());
        
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        
        // Sauvegarder le nouvel utilisateur
        userRepository.save(utilisateur);
        return ResponseEntity.ok("Account created successfully");
    }
}