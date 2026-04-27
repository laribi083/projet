package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Activity;
import com.votredomaine.modelememoire.model.Admin;
import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.model.Teacher;
import com.votredomaine.modelememoire.model.Utilisateur;
import com.votredomaine.modelememoire.repository.TeacherRepository;
import com.votredomaine.modelememoire.repository.UserRepository;
import com.votredomaine.modelememoire.service.ActivityService;
import com.votredomaine.modelememoire.service.AdminService;
import com.votredomaine.modelememoire.service.Courseservice;
import com.votredomaine.modelememoire.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
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
    
    @Autowired
    private ActivityService activityService;
    
    // ==================== PAGES ====================
    
    @GetMapping("/login")
    public String showLoginPage() {
        return "admin-login";
    }
    
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
    
    @GetMapping("/users")
    public String usersManagement(Model model, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        
        List<Utilisateur> students = userRepository.findAll();
        List<Teacher> teachers = teacherRepository.findAll();
        
        model.addAttribute("adminName", session.getAttribute("adminName"));
        model.addAttribute("adminEmail", session.getAttribute("adminEmail"));
        model.addAttribute("students", students);
        model.addAttribute("teachers", teachers);
        model.addAttribute("totalStudents", students.size());
        model.addAttribute("totalTeachers", teachers.size());
        
        return "htmladmin/user";
    }
    
    @GetMapping("/levels")
    public String educationalLevels(Model model, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        
        long firstYearCount = courseService.countByNiveau("1year");
        long secondYearCount = courseService.countByNiveau("2year");
        long thirdYearCount = courseService.countByNiveau("3year");
        
        model.addAttribute("adminName", session.getAttribute("adminName"));
        model.addAttribute("adminEmail", session.getAttribute("adminEmail"));
        model.addAttribute("firstYearCount", firstYearCount);
        model.addAttribute("secondYearCount", secondYearCount);
        model.addAttribute("thirdYearCount", thirdYearCount);
        
        return "htmladmin/level";
    }
    
    @GetMapping("/course-verification")
    public String courseVerification(Model model, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/admin/login";
        }
        
        List<Course> pendingCourses = courseService.findByStatus("PENDING");
        List<Course> validatedCourses = courseService.findByStatus("VALIDATED");
        List<Course> publishedCourses = courseService.findByStatus("PUBLISHED");
        
        model.addAttribute("adminName", session.getAttribute("adminName"));
        model.addAttribute("adminEmail", session.getAttribute("adminEmail"));
        model.addAttribute("pendingCourses", pendingCourses);
        model.addAttribute("validatedCourses", validatedCourses);
        model.addAttribute("publishedCourses", publishedCourses);
        
        return "htmladmin/valid";
    }
    
    // ==================== API AUTHENTIFICATION ====================
    
    @PostMapping("/api/register")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerAdmin(@RequestBody Map<String, String> registerData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String username = registerData.get("username");
            String email = registerData.get("email");
            String password = registerData.get("password");
            String fullName = registerData.get("fullName");
            
            System.out.println("📝 Creating admin: " + email);
            
            if (username == null || username.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Username is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (password == null || password.length() < 6) {
                response.put("success", false);
                response.put("message", "Password must be at least 6 characters");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (fullName == null || fullName.trim().isEmpty()) {
                fullName = username;
            }
            
            Admin admin = adminService.registerAdmin(username, email, password, fullName);
            
            response.put("success", true);
            response.put("message", "Admin created successfully");
            response.put("adminId", admin.getId());
            response.put("email", admin.getEmail());
            response.put("username", admin.getUsername());
            
            System.out.println("✅ ADMIN CREATED: " + admin.getEmail());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = loginData.get("email");
            String password = loginData.get("password");
            
            System.out.println("🔐 Admin login attempt: " + email);
            
            if (email == null || password == null) {
                response.put("success", false);
                response.put("message", "Email and password are required");
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
                response.put("message", "Login successful");
                response.put("redirectUrl", "/admin/dashboard");
                response.put("adminName", admin.getFullName());
                
                System.out.println("✅ ADMIN LOGGED IN: " + admin.getEmail());
            } else {
                response.put("success", false);
                response.put("message", "Invalid email or password");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/api/logout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
    }
    
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
    
    // ==================== STATISTICS API ====================
    
    @GetMapping("/api/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            long studentsCount = userRepository.count();
            long teachersCount = teacherRepository.count();
            long totalUsers = studentsCount + teachersCount;
            
            long publishedCourses = courseService.countPublishedCourses();
            long pendingCourses = courseService.countPendingCourses();
            long validatedCourses = courseService.countValidatedCourses();
            long totalCourses = publishedCourses + pendingCourses + validatedCourses;
            
            long totalQuizzes = quizService.getAllActiveQuizzes().size();
            
            stats.put("success", true);
            stats.put("totalUsers", totalUsers);
            stats.put("studentsCount", studentsCount);
            stats.put("teachersCount", teachersCount);
            stats.put("totalCourses", totalCourses);
            stats.put("publishedCourses", publishedCourses);
            stats.put("pendingCourses", pendingCourses);
            stats.put("validatedCourses", validatedCourses);
            stats.put("totalQuizzes", totalQuizzes);
            
            System.out.println("📊 STATS - Users: " + totalUsers + ", Published: " + publishedCourses);
            
        } catch (Exception e) {
            stats.put("success", false);
            stats.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/api/recent-activities")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getRecentActivities() {
        List<Map<String, Object>> activities = new ArrayList<>();
        
        try {
            List<Activity> recentActivities = activityService.getRecentActivities();
            
            for (Activity activity : recentActivities) {
                Map<String, Object> act = new HashMap<>();
                act.put("userName", activity.getUserName());
                act.put("message", activity.getMessage());
                act.put("timeAgo", activity.getTimeAgo());
                act.put("type", activity.getType());
                activities.add(act);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/api/pending-courses")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getPendingCourses() {
        List<Map<String, Object>> pendingCourses = new ArrayList<>();
        
        try {
            List<Course> courses = courseService.findByStatus("PENDING");
            
            for (Course course : courses) {
                Map<String, Object> courseMap = new HashMap<>();
                courseMap.put("id", course.getId());
                courseMap.put("title", course.getTitle());
                courseMap.put("teacherName", course.getTeacherName());
                courseMap.put("createdAt", course.getCreatedAt());
                courseMap.put("module", course.getModule());
                courseMap.put("niveau", course.getNiveau());
                pendingCourses.add(courseMap);
            }
            
            System.out.println("📚 Pending courses found: " + pendingCourses.size());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(pendingCourses);
    }
    
    // ==================== USER MANAGEMENT API (CRUD) ====================
    
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
    
    @GetMapping("/api/students")
    @ResponseBody
    public ResponseEntity<List<Utilisateur>> getStudents() {
        return ResponseEntity.ok(userRepository.findAll());
    }
    
    @GetMapping("/api/teachers")
    @ResponseBody
    public ResponseEntity<List<Teacher>> getTeachers() {
        return ResponseEntity.ok(teacherRepository.findAll());
    }
    
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
    
    // ==================== SUPPRESSION ET MODIFICATION ====================
    
    @DeleteMapping("/api/student/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteStudent(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Utilisateur> student = userRepository.findById(id);
            if (student.isEmpty()) {
                response.put("success", false);
                response.put("message", "Student not found");
                return ResponseEntity.notFound().build();
            }
            
            String studentName = student.get().getName();
            userRepository.deleteById(id);
            
            activityService.saveActivity(new Activity(
                "USER_DELETED",
                "deleted student account: " + studentName,
                "Admin",
                "ADMIN"
            ));
            
            response.put("success", true);
            response.put("message", "Student deleted successfully");
            System.out.println("🗑 Student deleted: " + studentName);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/api/teacher/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteTeacher(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Teacher> teacher = teacherRepository.findById(id);
            if (teacher.isEmpty()) {
                response.put("success", false);
                response.put("message", "Teacher not found");
                return ResponseEntity.notFound().build();
            }
            
            String teacherName = teacher.get().getName();
            teacherRepository.deleteById(id);
            
            activityService.saveActivity(new Activity(
                "USER_DELETED",
                "deleted teacher account: " + teacherName,
                "Admin",
                "ADMIN"
            ));
            
            response.put("success", true);
            response.put("message", "Teacher deleted successfully");
            System.out.println("🗑 Teacher deleted: " + teacherName);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/api/student/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStudent(@PathVariable Long id, @RequestBody Map<String, String> userData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Utilisateur> studentOpt = userRepository.findById(id);
            if (studentOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Student not found");
                return ResponseEntity.notFound().build();
            }
            
            Utilisateur student = studentOpt.get();
            String name = userData.get("name");
            String email = userData.get("email");
            
            if (name != null && !name.trim().isEmpty()) {
                student.setName(name);
            }
            
            if (email != null && !email.trim().isEmpty()) {
                Optional<Utilisateur> existingUser = userRepository.findByEmail(email);
                if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                    response.put("success", false);
                    response.put("message", "Email already in use");
                    return ResponseEntity.badRequest().body(response);
                }
                student.setEmail(email);
            }
            
            userRepository.save(student);
            
            activityService.saveActivity(new Activity(
                "USER_UPDATED",
                "updated student account: " + student.getName(),
                "Admin",
                "ADMIN"
            ));
            
            response.put("success", true);
            response.put("message", "Student updated successfully");
            response.put("student", student);
            System.out.println("✏️ Student updated: " + student.getName());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/api/teacher/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateTeacher(@PathVariable Long id, @RequestBody Map<String, String> teacherData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Teacher> teacherOpt = teacherRepository.findById(id);
            if (teacherOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Teacher not found");
                return ResponseEntity.notFound().build();
            }
            
            Teacher teacher = teacherOpt.get();
            String name = teacherData.get("name");
            String email = teacherData.get("email");
            String department = teacherData.get("department");
            String phone = teacherData.get("phone");
            
            if (name != null && !name.trim().isEmpty()) {
                teacher.setName(name);
            }
            
            if (email != null && !email.trim().isEmpty()) {
                Optional<Teacher> existingTeacher = teacherRepository.findByEmail(email);
                if (existingTeacher.isPresent() && !existingTeacher.get().getId().equals(id)) {
                    response.put("success", false);
                    response.put("message", "Email already in use");
                    return ResponseEntity.badRequest().body(response);
                }
                teacher.setEmail(email);
            }
            
            if (department != null) {
                teacher.setDepartment(department);
            }
            
            if (phone != null) {
                teacher.setPhone(phone);
            }
            
            teacherRepository.save(teacher);
            
            activityService.saveActivity(new Activity(
                "USER_UPDATED",
                "updated teacher account: " + teacher.getName(),
                "Admin",
                "ADMIN"
            ));
            
            response.put("success", true);
            response.put("message", "Teacher updated successfully");
            response.put("teacher", teacher);
            System.out.println("✏️ Teacher updated: " + teacher.getName());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== GESTION DES STATUTS DES COURS ====================
    
    @PutMapping("/api/course/{id}/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCourseStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Course course = courseService.getCourseById(id);
            if (course == null) {
                response.put("success", false);
                response.put("message", "Course not found");
                return ResponseEntity.notFound().build();
            }
            
            course.setStatus(status);
            courseService.save(course);
            
            // Enregistrer l'activité
            activityService.saveActivity(new Activity(
                "COURSE_VALIDATED",
                "validated course: " + course.getTitle(),
                "Admin",
                "ADMIN"
            ));
            
            response.put("success", true);
            response.put("message", "Course validated successfully!");
            
            System.out.println("✅ Course validated: " + course.getTitle());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    // ⭐ NOUVEAU: API pour PUBLIER un cours (VALIDATED -> PUBLISHED)
    @PutMapping("/api/course/{id}/publish")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> publishCourse(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Course course = courseService.getCourseById(id);
            if (course == null) {
                response.put("success", false);
                response.put("message", "Course not found");
                return ResponseEntity.notFound().build();
            }
            
            if (!"VALIDATED".equals(course.getStatus())) {
                response.put("success", false);
                response.put("message", "Only validated courses can be published");
                return ResponseEntity.badRequest().body(response);
            }
            
            course.setStatus("PUBLISHED");
            courseService.save(course);
            
            // Enregistrer l'activité
            activityService.saveActivity(new Activity(
                "COURSE_PUBLISHED",
                "published course: " + course.getTitle(),
                "Admin",
                "ADMIN"
            ));
            
            response.put("success", true);
            response.put("message", "Course published successfully!");
            
            System.out.println("✅ Course published: " + course.getTitle());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}