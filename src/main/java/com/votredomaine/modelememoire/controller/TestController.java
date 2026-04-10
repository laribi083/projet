package com.votredomaine.modelememoire.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/insert")
    public String insertTestUser() {
        try {
            String email = "test-" + System.currentTimeMillis() + "@test.com";
            String rawPassword = "password123";
            
            // ✅ Hasher le mot de passe avant insertion
            String hashedPassword = passwordEncoder.encode(rawPassword);
            
            jdbcTemplate.update(
                "INSERT INTO users (name, email, password) VALUES (?, ?, ?)",
                "Test User", email, hashedPassword
            );
            return "✅ Utilisateur inséré avec email: " + email + "\nPassword hashé: " + hashedPassword;
        } catch (Exception e) {
            return "❌ Erreur: " + e.getMessage();
        }
    }

    @GetMapping("/check")
    public List<Map<String, Object>> checkUsers() {
        List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT id, name, email, password FROM users ORDER BY id DESC");
        
        // Ajouter un indicateur pour voir si les mots de passe sont hashés
        for (Map<String, Object> user : users) {
            String password = (String) user.get("password");
            boolean isHashed = password != null && password.startsWith("$2a$");
            user.put("isHashed", isHashed);
            user.put("passwordPreview", isHashed ? password.substring(0, Math.min(30, password.length())) + "..." : password);
        }
        
        return users;
    }
    
    @GetMapping("/migrate")
    public String migratePasswords() {
        try {
            List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT id, password FROM users WHERE password NOT LIKE '$2a$%'");
            int updated = 0;
            
            for (Map<String, Object> user : users) {
                Long id = ((Number) user.get("id")).longValue();
                String oldPassword = (String) user.get("password");
                String hashedPassword = passwordEncoder.encode(oldPassword);
                
                jdbcTemplate.update("UPDATE users SET password = ? WHERE id = ?", hashedPassword, id);
                updated++;
            }
            
            return "✅ Migration terminée: " + updated + " mots de passe hashés";
        } catch (Exception e) {
            return "❌ Erreur: " + e.getMessage();
        }
    }
    
    @GetMapping("/test-login")
    public String testLogin(@RequestParam String email, @RequestParam String password) {
        try {
            Map<String, Object> user = jdbcTemplate.queryForMap(
                "SELECT id, name, email, password FROM users WHERE email = ?",
                email
            );
            
            String hashedPassword = (String) user.get("password");
            boolean matches = passwordEncoder.matches(password, hashedPassword);
            
            if (matches) {
                return "✅ Login réussi pour: " + email + "\nMot de passe valide!";
            } else {
                return "❌ Login échoué pour: " + email + "\nMot de passe incorrect!";
            }
        } catch (Exception e) {
            return "❌ Utilisateur non trouvé: " + email;
        }
    }
}