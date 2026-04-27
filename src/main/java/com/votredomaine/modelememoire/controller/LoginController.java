package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Admin;
import com.votredomaine.modelememoire.model.Teacher;
import com.votredomaine.modelememoire.model.Utilisateur;
import com.votredomaine.modelememoire.repository.UserRepository;
import com.votredomaine.modelememoire.service.AdminService;
import com.votredomaine.modelememoire.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private AdminService adminService;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public ModelAndView processLogin(@RequestParam String email, 
                                     @RequestParam String password,
                                     HttpSession session) {
        
        System.out.println("🔐 Tentative de connexion pour: " + email);
        
        // 1. Vérifier d'abord si c'est un ADMIN
        Optional<Admin> adminOpt = adminService.loginAdmin(email, password);
        
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            session.setAttribute("adminId", admin.getId());
            session.setAttribute("adminName", admin.getFullName());
            session.setAttribute("adminEmail", admin.getEmail());
            session.setAttribute("adminUsername", admin.getUsername());
            session.setAttribute("role", "ADMIN");
            session.setAttribute("loggedIn", true);
            
            System.out.println("✅ Connexion ADMIN réussie - ID: " + admin.getId());
            return new ModelAndView("redirect:/admin/dashboard");
        }
        
        // 2. Vérifier ensuite si c'est un TEACHER
        var teacherOpt = teacherService.findByEmail(email);
        
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            try {
                if (passwordEncoder.matches(password, teacher.getPassword())) {
                    session.setAttribute("teacherId", teacher.getId());
                    session.setAttribute("teacherName", teacher.getName());
                    session.setAttribute("teacherEmail", teacher.getEmail());
                    session.setAttribute("userId", teacher.getId());
                    session.setAttribute("userName", teacher.getName());
                    session.setAttribute("userEmail", teacher.getEmail());
                    session.setAttribute("role", "TEACHER");
                    session.setAttribute("loggedIn", true);
                    
                    System.out.println("✅ Connexion TEACHER réussie - ID: " + teacher.getId());
                    return new ModelAndView("redirect:/teacher/dashboard");
                }
            } catch (Exception e) {
                System.out.println("⚠️ Erreur BCrypt pour teacher: " + e.getMessage());
            }
        }
        
        // 3. Vérifier dans la table users (étudiants)
        var userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            Utilisateur user = userOpt.get();
            try {
                if (passwordEncoder.matches(password, user.getPassword())) {
                    session.setAttribute("userId", user.getId());
                    session.setAttribute("userName", user.getName());
                    session.setAttribute("userEmail", user.getEmail());
                    session.setAttribute("role", "STUDENT");
                    session.setAttribute("loggedIn", true);
                    
                    System.out.println("✅ Connexion STUDENT réussie - ID: " + user.getId());
                    return new ModelAndView("redirect:/student/dashboard");
                }
            } catch (Exception e) {
                System.out.println("⚠️ Erreur BCrypt pour student: " + e.getMessage());
            }
        }
        
        System.out.println("❌ Connexion échouée pour: " + email);
        ModelAndView mav = new ModelAndView("login");
        mav.addObject("error", "Email ou mot de passe incorrect");
        return mav;
    }
    
    // ⚠️ SUPPRIMEZ CETTE MÉTHODE - Elle va dans DashboardController ⚠️
    // @GetMapping("/student/dashboard")
    // public ModelAndView studentDashboard(HttpSession session) { ... }
    
    @GetMapping("/teacher/quiz")
    public ModelAndView teacherQuiz(HttpSession session) {
        if (session.getAttribute("role") == null || !"TEACHER".equals(session.getAttribute("role"))) {
            return new ModelAndView("redirect:/login");
        }
        
        ModelAndView mav = new ModelAndView("htmlTeacher/quiz");
        mav.addObject("teacherName", session.getAttribute("teacherName"));
        return mav;
    }
    
    @GetMapping("/teacher/create-course")
    public ModelAndView createCourse(HttpSession session) {
        if (session.getAttribute("role") == null || !"TEACHER".equals(session.getAttribute("role"))) {
            return new ModelAndView("redirect:/login");
        }
        
        ModelAndView mav = new ModelAndView("htmlTeacher/create-course");
        mav.addObject("teacherName", session.getAttribute("teacherName"));
        return mav;
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}