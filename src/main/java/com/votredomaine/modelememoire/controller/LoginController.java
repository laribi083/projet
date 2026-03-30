package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Teacher;
import com.votredomaine.modelememoire.model.Utilisateur;
import com.votredomaine.modelememoire.repository.UserRepository;
import com.votredomaine.modelememoire.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TeacherService teacherService;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // Traitement du formulaire POST
    @PostMapping("/login")
    public ModelAndView processLogin(@RequestParam String email, 
                                     @RequestParam String password,
                                     HttpSession session) {
        
        System.out.println("🔐 Tentative de connexion pour: " + email);
        
        // 1. Vérifier d'abord si c'est un TEACHER
        var teacherOpt = teacherService.findByEmail(email);
        
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            if (passwordEncoder.matches(password, teacher.getPassword())) {
                // Pour courcontroller
                session.setAttribute("teacherId", teacher.getId());
                session.setAttribute("teacherName", teacher.getName());
                session.setAttribute("teacherEmail", teacher.getEmail());
                // Pour compatibilité
                session.setAttribute("userId", teacher.getId());
                session.setAttribute("userName", teacher.getName());
                session.setAttribute("userEmail", teacher.getEmail());
                session.setAttribute("role", "TEACHER");
                session.setAttribute("loggedIn", true);
                
                System.out.println("✅ Connexion TEACHER réussie - ID: " + teacher.getId());
                return new ModelAndView("redirect:/teacher/dashboard");
            } else {
                ModelAndView mav = new ModelAndView("login");
                mav.addObject("error", "Email ou mot de passe incorrect");
                return mav;
            }
        }
        
        // 2. Vérifier dans la table users (étudiants)
        var userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            Utilisateur user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                session.setAttribute("userId", user.getId());
                session.setAttribute("userName", user.getName());
                session.setAttribute("userEmail", user.getEmail());
                session.setAttribute("role", "STUDENT");
                session.setAttribute("loggedIn", true);
                
                
            }
        }
        
        System.out.println("❌ Connexion échouée pour: " + email);
        ModelAndView mav = new ModelAndView("login");
        mav.addObject("error", "Email ou mot de passe incorrect");
        return mav;
    }
    
    // Dashboard étudiant
    @GetMapping("/student/dashboard")
    public ModelAndView studentDashboard(HttpSession session) {
        if (session.getAttribute("role") == null || !"STUDENT".equals(session.getAttribute("role"))) {
            return new ModelAndView("redirect:/login");
        }
        
        ModelAndView mav = new ModelAndView("htmlstudent/Dashboard");
        mav.addObject("userName", session.getAttribute("userName"));
        mav.addObject("studentName", session.getAttribute("userName"));
        mav.addObject("studentEmail", session.getAttribute("userEmail"));
        mav.addObject("niveau", session.getAttribute("niveau"));
        mav.addObject("filiere", session.getAttribute("filiere"));
        return mav;
    }
    
    // Route pour les quiz teacher
    @GetMapping("/teacher/quiz")
    public ModelAndView teacherQuiz(HttpSession session) {
        if (session.getAttribute("role") == null || !"TEACHER".equals(session.getAttribute("role"))) {
            return new ModelAndView("redirect:/login");
        }
        
        ModelAndView mav = new ModelAndView("htmlTeacher/quiz");
        mav.addObject("teacherName", session.getAttribute("teacherName"));
        return mav;
    }
    
    // Route pour créer un cours
    @GetMapping("/teacher/create-course")
    public ModelAndView createCourse(HttpSession session) {
        if (session.getAttribute("role") == null || !"TEACHER".equals(session.getAttribute("role"))) {
            return new ModelAndView("redirect:/login");
        }
        
        ModelAndView mav = new ModelAndView("htmlTeacher/create-course");
        mav.addObject("teacherName", session.getAttribute("teacherName"));
        return mav;
    }
    
    // Déconnexion
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}