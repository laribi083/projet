package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Admin;
import com.votredomaine.modelememoire.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    // ==================== PAGES ====================
    
    /**
     * Page de connexion admin
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "admin-login";
    }
    
    /**
     * Dashboard admin
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        
        model.addAttribute("adminName", session.getAttribute("adminName"));
        model.addAttribute("adminEmail", session.getAttribute("adminEmail"));
        model.addAttribute("adminUsername", session.getAttribute("adminUsername"));
        
        return "htmladmin/dashboard";
    }
    
    // ==================== API ====================
    
    /**
     * Inscription d'un nouvel administrateur
     */
    @PostMapping("/api/register")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerAdmin(@RequestBody Map<String, String> registerData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String username = registerData.get("username");
            String email = registerData.get("email");
            String password = registerData.get("password");
            String fullName = registerData.get("fullName");
            
            System.out.println("📝 Création d'admin: " + email);
            
            // Validation
            if (username == null || username.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Nom d'utilisateur requis");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email requis");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (password == null || password.length() < 6) {
                response.put("success", false);
                response.put("message", "Le mot de passe doit contenir au moins 6 caractères");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (fullName == null || fullName.trim().isEmpty()) {
                fullName = username;
            }
            
            Admin admin = adminService.registerAdmin(username, email, password, fullName);
            
            response.put("success", true);
            response.put("message", "Admin créé avec succès");
            response.put("adminId", admin.getId());
            response.put("email", admin.getEmail());
            response.put("username", admin.getUsername());
            
            System.out.println("✅ ADMIN CRÉÉ: " + admin.getEmail());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Connexion administrateur (API)
     */
    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = loginData.get("email");
            String password = loginData.get("password");
            
            System.out.println("🔐 Tentative de connexion admin: " + email);
            
            if (email == null || password == null) {
                response.put("success", false);
                response.put("message", "Email et mot de passe requis");
                return ResponseEntity.badRequest().body(response);
            }
            
            Optional<Admin> adminOpt = adminService.loginAdmin(email, password);
            
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                session.setAttribute("adminId", admin.getId());
                session.setAttribute("adminName", admin.getFullName());
                session.setAttribute("adminEmail", admin.getEmail());
                session.setAttribute("adminUsername", admin.getUsername());
                session.setAttribute("role", "ADMIN");
                session.setAttribute("loggedIn", true);
                
                response.put("success", true);
                response.put("message", "Connexion réussie");
                response.put("redirectUrl", "/admin/dashboard");
                response.put("adminName", admin.getFullName());
                
                System.out.println("✅ ADMIN CONNECTÉ: " + admin.getEmail());
            } else {
                response.put("success", false);
                response.put("message", "Email ou mot de passe incorrect");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Déconnexion administrateur
     */
    @PostMapping("/api/logout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Déconnexion réussie");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Vérifier si l'admin est connecté
     */
    @GetMapping("/api/check-session")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkSession(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        Long adminId = (Long) session.getAttribute("adminId");
        
        if (adminId != null) {
            response.put("loggedIn", true);
            response.put("adminName", session.getAttribute("adminName"));
            response.put("adminEmail", session.getAttribute("adminEmail"));
        } else {
            response.put("loggedIn", false);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Récupérer tous les administrateurs (pour debug)
     */
    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllAdmins() {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("admins", adminService.getAllAdmins());
            response.put("count", adminService.countAdmins());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}