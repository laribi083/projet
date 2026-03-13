package com.votredomaine.modelememoire.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/insert")
    public String insertTestUser() {
        try {
            String email = "test-" + System.currentTimeMillis() + "@test.com";
            jdbcTemplate.update(
                "INSERT INTO users (name, email, password) VALUES (?, ?, ?)",
                "Test User", email, "password123"
            );
            return "✅ Utilisateur inséré avec email: " + email;
        } catch (Exception e) {
            return "❌ Erreur: " + e.getMessage();
        }
    }

    @GetMapping("/check")
    public List<Map<String, Object>> checkUsers() {
        return jdbcTemplate.queryForList("SELECT * FROM users ORDER BY id DESC");
    }
}