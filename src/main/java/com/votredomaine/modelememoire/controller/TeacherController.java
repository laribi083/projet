package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.model.Quiz;
import com.votredomaine.modelememoire.model.Teacher;
import com.votredomaine.modelememoire.service.Courseservice;
import com.votredomaine.modelememoire.service.EnrollmentService;
import com.votredomaine.modelememoire.service.QuizService;
import com.votredomaine.modelememoire.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/teacher")
public class TeacherController {
    
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private Courseservice courseService;
    
    @Autowired
    private QuizService quizService;
    
    @Autowired
    private EnrollmentService enrollmentService;
   
    // ==================== GESTION DES ENSEIGNANTS (API) ====================
    
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<?> registerTeacher(@RequestBody Map<String, String> request) {
        try {
            Teacher teacher = teacherService.registerTeacher(
                request.get("name"),
                request.get("email"),
                request.get("password"),
                request.get("department"),
                request.get("phone")
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Teacher registered successfully");
            response.put("teacherId", teacher.getId());
            response.put("name", teacher.getName());
            response.put("email", teacher.getEmail());
            response.put("department", teacher.getDepartment());
            response.put("phone", teacher.getPhone());
            
            System.out.println("✅ NOUVEAU TEACHER CRÉÉ: " + teacher.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/dashboard/{email}")
    @ResponseBody
    public ResponseEntity<?> getTeacherDashboard(@PathVariable String email) {
        Optional<Teacher> teacherOpt = teacherService.findByEmail(email);
        
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("teacher", Map.of(
                "id", teacher.getId(),
                "name", teacher.getName(),
                "email", teacher.getEmail(),
                "department", teacher.getDepartment(),
                "phone", teacher.getPhone()
            ));
            dashboard.put("message", "Bienvenue dans l'interface Teacher!");
            
            List<Course> courses = courseService.findByTeacherId(teacher.getId());
            dashboard.put("courses", courses);
            
            return ResponseEntity.ok(dashboard);
        }
        
        return ResponseEntity.status(404).body("Teacher not found");
    }
    
    @GetMapping("/profile/{email}")
    @ResponseBody
    public ResponseEntity<?> getTeacherProfile(@PathVariable String email) {
        Optional<Teacher> teacherOpt = teacherService.findByEmail(email);
        
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", teacher.getId());
            profile.put("name", teacher.getName());
            profile.put("email", teacher.getEmail());
            profile.put("department", teacher.getDepartment());
            profile.put("phone", teacher.getPhone());
            profile.put("createdAt", teacher.getCreatedAt());
            
            return ResponseEntity.ok(profile);
        }
        
        return ResponseEntity.status(404).body("Teacher not found");
    }
    
    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<?> getAllTeachers() {
        try {
            List<Teacher> teachers = teacherService.getAllTeachers();
            
            if (teachers == null || teachers.isEmpty()) {
                return ResponseEntity.ok(Map.of("message", "Aucun enseignant trouvé", "count", 0));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("count", teachers.size());
            response.put("teachers", teachers);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    // ==================== GESTION DES COURS (API) ====================
    
    @PostMapping("/api/courses")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createCourse(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("niveau") String niveau,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            String teacherName = (String) session.getAttribute("teacherName");
            
            if (teacherId == null) {
                response.put("success", false);
                response.put("message", "Session expirée");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            if (teacherName == null) {
                teacherName = "Professeur";
            }
            
            Course course = new Course();
            course.setTitle(title);
            course.setDescription(description);
            course.setNiveau(extractNiveauLevel(niveau));
            course.setModule(niveau);
            course.setTeacherId(teacherId);
            course.setTeacherName(teacherName);
            course.setStatus("ACTIVE");
            course.setCreatedAt(LocalDateTime.now());
            course.setUpdatedAt(LocalDateTime.now());
            
            Course savedCourse = courseService.save(course);
            
            response.put("success", true);
            response.put("course", savedCourse);
            response.put("courseId", savedCourse.getId());
            response.put("message", "Cours créé avec succès");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/teacher-courses")
    @ResponseBody
    public ResponseEntity<List<Course>> getTeacherCourses(HttpSession session) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        if (teacherId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Course> courses = courseService.getCoursesByTeacherId(teacherId);
        return ResponseEntity.ok(courses != null ? courses : List.of());
    }
    
    @GetMapping("/my-courses")
    @ResponseBody
    public ResponseEntity<List<Course>> getMyCourses(HttpSession session) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        if (teacherId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Course> courses = courseService.getCoursesByTeacherId(teacherId);
        
        if (courses != null) {
            for (Course course : courses) {
                long quizCount = quizService.countQuizzesByCourse(course.getId());
                course.setQuizCount((int) quizCount);
                
                // ⭐ AJOUTER LE NOMBRE D'ÉTUDIANTS POUR CHAQUE COURS
                long studentCount = enrollmentService.countStudentsByCourse(course.getId());
                course.setTotalStudents((int) studentCount);
            }
        }
        
        return ResponseEntity.ok(courses != null ? courses : List.of());
    }
    
    // ⭐ NOUVEL ENDPOINT POUR LES STATISTIQUES GLOBALES
    @GetMapping("/api/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTeacherStats(HttpSession session) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        if (teacherId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Map<String, Object> stats = new HashMap<>();
        
        // Nombre de cours
        List<Course> courses = courseService.getCoursesByTeacherId(teacherId);
        stats.put("totalCourses", courses.size());
        
        // Nombre de quiz
        List<Quiz> quizzes = quizService.getQuizzesByTeacher(teacherId);
        stats.put("totalQuizzes", quizzes.size());
        
        // ⭐ NOMBRE TOTAL D'ÉTUDIANTS INSCRITS (unique)
        long totalStudents = enrollmentService.countTotalStudentsByTeacher(teacherId);
        stats.put("totalStudents", totalStudents);
        
        return ResponseEntity.ok(stats);
    }
    
    @DeleteMapping("/delete-course/{courseId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable Long courseId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            
            if (teacherId == null) {
                response.put("success", false);
                response.put("message", "Session expirée");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            if (courseId == null) {
                response.put("success", false);
                response.put("message", "ID du cours manquant");
                return ResponseEntity.badRequest().body(response);
            }
            
            Course course = courseService.getCourseById(courseId);
            
            if (course == null) {
                response.put("success", false);
                response.put("message", "Cours non trouvé");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            if (course.getTeacherId() == null || !course.getTeacherId().equals(teacherId)) {
                response.put("success", false);
                response.put("message", "Accès non autorisé");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            List<Quiz> quizzes = quizService.getQuizzesByCourse(courseId);
            if (quizzes != null && !quizzes.isEmpty()) {
                for (Quiz quiz : quizzes) {
                    try {
                        quizService.deleteQuiz(quiz.getId());
                    } catch (Exception e) {
                        System.err.println("Erreur lors de la suppression du quiz " + quiz.getId() + ": " + e.getMessage());
                    }
                }
            }
            
            courseService.deleteCourse(courseId);
            response.put("success", true);
            response.put("message", "Cours et ses quiz supprimés avec succès");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // ==================== MODIFICATION DE COURS (API) ====================
    
    @PostMapping("/api/update-course/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCourseApi(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("module") String module,
            @RequestParam("niveau") String niveau,
            @RequestParam(value = "newFiles", required = false) List<MultipartFile> newFiles,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            
            if (teacherId == null) {
                response.put("success", false);
                response.put("message", "Session expirée");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            Course existingCourse = courseService.getCourseById(id);
            
            if (existingCourse == null) {
                response.put("success", false);
                response.put("message", "Cours non trouvé");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            if (!existingCourse.getTeacherId().equals(teacherId)) {
                response.put("success", false);
                response.put("message", "Accès non autorisé");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            existingCourse.setTitle(title);
            existingCourse.setDescription(description);
            existingCourse.setModule(module);
            existingCourse.setNiveau(niveau);
            existingCourse.setUpdatedAt(LocalDateTime.now());
            
            if (newFiles != null && !newFiles.isEmpty()) {
                List<String> filePaths = existingCourse.getFilePaths();
                List<String> fileNames = existingCourse.getFileNames();
                
                if (filePaths == null) {
                    filePaths = new ArrayList<>();
                    fileNames = new ArrayList<>();
                }
                
                for (MultipartFile file : newFiles) {
                    if (!file.isEmpty()) {
                        String originalName = file.getOriginalFilename();
                        String uniqueName = UUID.randomUUID().toString() + "_" + originalName;
                        String uploadPath = "uploads/courses/" + uniqueName;
                        
                        Path path = Paths.get(uploadPath);
                        Files.createDirectories(path.getParent());
                        Files.write(path, file.getBytes());
                        
                        filePaths.add(uploadPath);
                        fileNames.add(originalName);
                    }
                }
                
                existingCourse.setFilePaths(filePaths);
                existingCourse.setFileNames(fileNames);
                
                if (!filePaths.isEmpty()) {
                    existingCourse.setFilePath(filePaths.get(0));
                    existingCourse.setFileName(fileNames.get(0));
                }
            }
            
            Course updatedCourse = courseService.save(existingCourse);
            
            response.put("success", true);
            response.put("message", "Cours modifié avec succès");
            response.put("courseId", updatedCourse.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // ==================== VISUALISATION DES COURS (PAGES HTML) ====================
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        String teacherName = (String) session.getAttribute("teacherName");
        String teacherEmail = (String) session.getAttribute("teacherEmail");
        
        if (teacherId == null) {
            return "redirect:/login";
        }
        
        List<Course> courses = courseService.getCoursesByTeacherId(teacherId);
        
        if (courses != null) {
            for (Course course : courses) {
                long quizCount = quizService.countQuizzesByCourse(course.getId());
                course.setQuizCount((int) quizCount);
            }
        }
        
        model.addAttribute("teacherName", teacherName != null ? teacherName : "Professeur");
        model.addAttribute("teacherEmail", teacherEmail != null ? teacherEmail : "");
        model.addAttribute("courses", courses != null ? courses : List.of());
        
        return "htmlTeacher/dashbord";
    }
    
    @GetMapping("/view-course/{courseId}")
    public String viewCourse(@PathVariable Long courseId, Model model, HttpSession session) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        if (teacherId == null) {
            return "redirect:/login";
        }
        
        if (courseId == null) {
            return "redirect:/teacher/dashboard";
        }
        
        Course course = courseService.getCourseById(courseId);
        if (course == null || course.getTeacherId() == null || !course.getTeacherId().equals(teacherId)) {
            return "redirect:/teacher/dashboard";
        }
        
        List<Quiz> quizzes = quizService.getQuizzesByCourse(courseId);
        
        model.addAttribute("course", course);
        model.addAttribute("quizzes", quizzes != null ? quizzes : List.of());
        
        return "htmlTeacher/course-view";
    }
    
    @GetMapping("/course/{courseId}")
    @ResponseBody
    public ResponseEntity<Course> getCourseById(@PathVariable Long courseId) {
        if (courseId == null) {
            return ResponseEntity.badRequest().build();
        }
        Course course = courseService.getCourseById(courseId);
        if (course != null) {
            return ResponseEntity.ok(course);
        }
        return ResponseEntity.notFound().build();
    }
    
    // ==================== GESTION DES FICHIERS ====================
    
    @GetMapping("/preview-pdf")
    @ResponseBody
    public ResponseEntity<?> previewPDF(@RequestParam String path, HttpSession session) {
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            if (teacherId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            if (path == null || path.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            String decodedPath = URLDecoder.decode(path, "UTF-8");
            File file = new File(decodedPath);
            
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            Path filePath = Paths.get(decodedPath);
            byte[] content = Files.readAllBytes(filePath);
            String mimeType = Files.probeContentType(filePath);
            
            if (mimeType == null) {
                mimeType = "application/pdf";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                    .body(content);
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/download-file")
    @ResponseBody
    public ResponseEntity<?> downloadFile(@RequestParam String path, @RequestParam String name, HttpSession session) {
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            if (teacherId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            if (path == null || path.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            String decodedPath = URLDecoder.decode(path, "UTF-8");
            String decodedName = URLDecoder.decode(name, "UTF-8");
            File file = new File(decodedPath);
            
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            Path filePath = Paths.get(decodedPath);
            byte[] content = Files.readAllBytes(filePath);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + decodedName + "\"")
                    .body(content);
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ==================== GESTION DES QUIZ ====================
    
    @GetMapping("/api/quizzes")
    @ResponseBody
    public ResponseEntity<List<Quiz>> getTeacherQuizzes(HttpSession session) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        if (teacherId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Quiz> quizzes = quizService.getQuizzesByTeacher(teacherId);
        return ResponseEntity.ok(quizzes != null ? quizzes : List.of());
    }
    
    @DeleteMapping("/api/delete-quiz/{quizId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteQuizApi(@PathVariable Long quizId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            
            if (teacherId == null) {
                response.put("success", false);
                response.put("message", "Session expirée");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            Quiz quiz = quizService.getQuizById(quizId);
            
            if (quiz == null) {
                response.put("success", false);
                response.put("message", "Quiz non trouvé");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            if (!quiz.getTeacherId().equals(teacherId)) {
                response.put("success", false);
                response.put("message", "Accès non autorisé");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            quizService.deleteQuiz(quizId);
            
            response.put("success", true);
            response.put("message", "Quiz supprimé avec succès");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/api/create-quiz")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createQuiz(@RequestBody Map<String, Object> quizData, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            String teacherName = (String) session.getAttribute("teacherName");
            
            if (teacherId == null) {
                response.put("success", false);
                response.put("message", "Session expirée");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            if (quizData == null || quizData.isEmpty()) {
                response.put("success", false);
                response.put("message", "Données du quiz invalides");
                return ResponseEntity.badRequest().body(response);
            }
            
            Object courseIdObj = quizData.get("courseId");
            if (courseIdObj == null) {
                response.put("success", false);
                response.put("message", "ID du cours manquant");
                return ResponseEntity.badRequest().body(response);
            }
            
            Long courseId = Long.valueOf(courseIdObj.toString());
            String title = quizData.get("title") != null ? quizData.get("title").toString() : null;
            
            if (title == null || title.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Le titre du quiz est obligatoire");
                return ResponseEntity.badRequest().body(response);
            }
            
            String description = quizData.get("description") != null ? quizData.get("description").toString() : "";
            Integer timeLimit = quizData.get("timeLimit") != null ? Integer.valueOf(quizData.get("timeLimit").toString()) : 30;
            Integer passingScore = quizData.get("passingScore") != null ? Integer.valueOf(quizData.get("passingScore").toString()) : 70;
            String courseModule = quizData.get("courseModule") != null ? quizData.get("courseModule").toString() : "";
            String courseNiveau = quizData.get("courseNiveau") != null ? quizData.get("courseNiveau").toString() : "";
            
            Quiz quiz = new Quiz();
            quiz.setTitle(title);
            quiz.setDescription(description);
            quiz.setCourseId(courseId);
            quiz.setTeacherId(teacherId);
            quiz.setTeacherName(teacherName != null ? teacherName : "Professeur");
            quiz.setModule(courseModule);
            quiz.setNiveau(courseNiveau);
            quiz.setTimeLimit(timeLimit);
            quiz.setPassingScore(passingScore);
            quiz.setStatus("ACTIVE");
            quiz.setCreatedAt(LocalDateTime.now());
            quiz.setUpdatedAt(LocalDateTime.now());
            quiz.setTotalQuestions(0);
            
            Quiz savedQuiz = quizService.saveQuiz(quiz);
            
            if (savedQuiz == null) {
                response.put("success", false);
                response.put("message", "Erreur lors de la sauvegarde du quiz");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
            response.put("success", true);
            response.put("quizId", savedQuiz.getId());
            response.put("message", "Quiz créé avec succès");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/courses/niveau/{niveau}")
    @ResponseBody
    public ResponseEntity<List<Course>> getCoursesByNiveau(@PathVariable String niveau) {
        if (niveau == null || niveau.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Course> courses = courseService.findByNiveau(niveau);
        return ResponseEntity.ok(courses != null ? courses : List.of());
    }
    
    // ==================== METHODES PRIVEES ====================
    
    private String extractNiveauLevel(String moduleValue) {
        if (moduleValue == null) return "3year";
        
        String[] firstYearModules = {
            "Algebra 01", "Algebra 02", "Analysis 01", "Analysis 02",
            "Advanced Data Structures 01", "Information Coding & Representation",
            "Component & Representation of Information", "Introduction to project-oriented programming",
            "Machine Structure", "Electricity & Electronics"
        };
        
        String[] secondYearModules = {
            "Advanced Data Structures 02", "Computer Architecture", "DataBases and SQL",
            "Software ingenery 01", "Logique mathématiques", "advanced project-oriented programming",
            "comunication network 01", "operating system", "langages theorique", "web development 01"
        };
        
        for (String m : firstYearModules) {
            if (m.equals(moduleValue)) return "1year";
        }
        
        for (String m : secondYearModules) {
            if (m.equals(moduleValue)) return "2year";
        }
        
        return "3year";
    }
}