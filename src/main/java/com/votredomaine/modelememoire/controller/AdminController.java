package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Admin;
import com.votredomaine.modelememoire.model.Teacher;
import com.votredomaine.modelememoire.model.Utilisateur;
import com.votredomaine.modelememoire.repository.TeacherRepository;
import com.votredomaine.modelememoire.repository.UserRepository;
import com.votredomaine.modelememoire.service.AdminService;
import com.votredomaine.modelememoire.service.Courseservice;
import com.votredomaine.modelememoire.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private Courseservice courseService;
    
    @Autowired
    private QuizService quizService;
    
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
    
    // ==================== API ADMIN (Authentification) ====================
    
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
    
    // ==================== API STATISTIQUES (Dashboard) ====================
    
    /**
     * ⭐ STATISTIQUES POUR LE DASHBOARD ADMIN
     * Compte les étudiants (users) + les enseignants (teachers) + cours + quiz
     */
    @GetMapping("/api/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Compter les étudiants (users)
            long studentsCount = userRepository.count();
            
            // Compter les enseignants (teachers)
            long teachersCount = teacherRepository.count();
            
            // ⭐ Total des utilisateurs (étudiants + enseignants) - SAUF admins
            long totalUsers = studentsCount + teachersCount;
            
            // Compter les cours
            long totalCourses = courseService.findAll().size();
            
            // Compter les quiz
            long totalQuizzes = quizService.getAllActiveQuizzes().size();
            
            // Cours en attente (cours sans fichiers)
            long pendingCourses = courseService.findAll().stream()
                .filter(c -> c.getFilePaths() == null || c.getFilePaths().isEmpty())
                .count();
            
            // Cours validés
            long validatedCourses = totalCourses - pendingCourses;
            
            stats.put("success", true);
            stats.put("totalUsers", totalUsers);
            stats.put("studentsCount", studentsCount);
            stats.put("teachersCount", teachersCount);
            stats.put("totalCourses", totalCourses);
            stats.put("totalQuizzes", totalQuizzes);
            stats.put("pendingCourses", pendingCourses);
            stats.put("validatedCourses", validatedCourses);
            
            System.out.println("📊 Stats Admin - Utilisateurs: " + totalUsers + 
                               " (Étudiants: " + studentsCount + 
                               ", Enseignants: " + teachersCount + 
                               ", Cours: " + totalCourses + 
                               ", Quiz: " + totalQuizzes + ")");
            
        } catch (Exception e) {
            stats.put("success", false);
            stats.put("error", e.getMessage());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(stats);
    }
    
    // ==================== API GESTION DES UTILISATEURS ====================
    
    /**
     * ⭐ LISTE DE TOUS LES UTILISATEURS (Étudiants + Enseignants)
     */
    @GetMapping("/api/all-users")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Utilisateur> students = userRepository.findAll();
            List<Teacher> teachers = teacherRepository.findAll();
            
            response.put("success", true);
            response.put("students", students);
            response.put("studentsCount", students.size());
            response.put("teachers", teachers);
            response.put("teachersCount", teachers.size());
            response.put("totalUsers", students.size() + teachers.size());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * ⭐ LISTE DES ÉTUDIANTS UNIQUEMENT
     */
    @GetMapping("/api/students")
    @ResponseBody
    public ResponseEntity<List<Utilisateur>> getStudents() {
        return ResponseEntity.ok(userRepository.findAll());
    }
    
    /**
     * ⭐ LISTE DES ENSEIGNANTS UNIQUEMENT
     */
    @GetMapping("/api/teachers")
    @ResponseBody
    public ResponseEntity<List<Teacher>> getTeachers() {
        return ResponseEntity.ok(teacherRepository.findAll());
    }
    
    /**
     * ⭐ LISTE DES ADMINISTRATEURS
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