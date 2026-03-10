package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Utilisateur;
import com.votredomaine.modelememoire.repository.UserRepository;
import com.votredomaine.modelememoire.service.createservice;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import java.util.Optional;

@RestController
@RequestMapping("/api/inscription")
@CrossOrigin("*")
public class InscriptionController {
    
    @Autowired
    private createservice createService;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<String> inscription(@RequestBody Utilisateur utilisateur) {
       
        Optional<Utilisateur> existingUser = userRepository.findByEmail(utilisateur.getEmail());
        
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        
       
        userRepository.save(utilisateur);
        return ResponseEntity.ok("Account created successfully");
    }
}