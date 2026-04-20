package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.model.Question;
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
            
            System.out.println("✅ NEW TEACHER CREATED: " + teacher.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = loginData.get("email");
            String password = loginData.get("password");
            
            System.out.println("🔐 Teacher login attempt: " + email);
            
            Optional<Teacher> teacherOpt = teacherService.loginTeacher(email, password);
            
            if (teacherOpt.isPresent()) {
                Teacher teacher = teacherOpt.get();
                session.setAttribute("teacherId", teacher.getId());
                session.setAttribute("teacherName", teacher.getName());
                session.setAttribute("teacherEmail", teacher.getEmail());
                session.setAttribute("role", "TEACHER");
                session.setAttribute("loggedIn", true);
                
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("redirectUrl", "/teacher/dashboard");
                response.put("teacherName", teacher.getName());
                
                System.out.println("✅ TEACHER LOGGED IN: " + teacher.getEmail() + " (ID: " + teacher.getId() + ")");
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
            dashboard.put("message", "Welcome to Teacher Interface!");
            
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
                return ResponseEntity.ok(Map.of("message", "No teachers found", "count", 0));
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
                response.put("message", "Session expired");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            if (teacherName == null) {
                teacherName = "Professor";
            }
            
            Course course = new Course();
            course.setTitle(title);
            course.setDescription(description);
            course.setNiveau(extractNiveauLevel(niveau));
            course.setModule(niveau);
            course.setTeacherId(teacherId);
            course.setTeacherName(teacherName);
            course.setStatus("PENDING");
            course.setCreatedAt(LocalDateTime.now());
            course.setUpdatedAt(LocalDateTime.now());
            
            Course savedCourse = courseService.save(course);
            
            response.put("success", true);
            response.put("course", savedCourse);
            response.put("courseId", savedCourse.getId());
            response.put("message", "Course created successfully (pending validation)");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
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
                
                long studentCount = enrollmentService.countStudentsByCourse(course.getId());
                course.setTotalStudents((int) studentCount);
            }
        }
        
        return ResponseEntity.ok(courses != null ? courses : List.of());
    }
    
    @GetMapping("/course/{courseId}")
    @ResponseBody
    public ResponseEntity<Course> getCourseById(@PathVariable Long courseId, HttpSession session) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        if (teacherId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Course course = courseService.getCourseById(courseId);
        if (course == null || !course.getTeacherId().equals(teacherId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        return ResponseEntity.ok(course);
    }
    
    @DeleteMapping("/delete-course/{courseId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable Long courseId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            
            if (teacherId == null) {
                response.put("success", false);
                response.put("message", "Session expired");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            if (courseId == null) {
                response.put("success", false);
                response.put("message", "Course ID missing");
                return ResponseEntity.badRequest().body(response);
            }
            
            Course course = courseService.getCourseById(courseId);
            
            if (course == null) {
                response.put("success", false);
                response.put("message", "Course not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            if (course.getTeacherId() == null || !course.getTeacherId().equals(teacherId)) {
                response.put("success", false);
                response.put("message", "Unauthorized access");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            List<Quiz> quizzes = quizService.getQuizzesByCourse(courseId);
            if (quizzes != null && !quizzes.isEmpty()) {
                for (Quiz quiz : quizzes) {
                    try {
                        quizService.deleteQuiz(quiz.getId());
                    } catch (Exception e) {
                        System.err.println("Error deleting quiz " + quiz.getId() + ": " + e.getMessage());
                    }
                }
            }
            
            courseService.deleteCourse(courseId);
            response.put("success", true);
            response.put("message", "Course and its quizzes deleted successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
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
                response.put("message", "Session expired");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            Course existingCourse = courseService.getCourseById(id);
            
            if (existingCourse == null) {
                response.put("success", false);
                response.put("message", "Course not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            if (!existingCourse.getTeacherId().equals(teacherId)) {
                response.put("success", false);
                response.put("message", "Unauthorized access");
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
            response.put("message", "Course modified successfully");
            response.put("courseId", updatedCourse.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // ==================== VISUALISATION DES COURS (PAGES HTML) ====================
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        String teacherName = (String) session.getAttribute("teacherName");
        String teacherEmail = (String) session.getAttribute("teacherEmail");
        
        System.out.println("🔍 SESSION TEACHER ID: " + teacherId);
        System.out.println("🔍 SESSION TEACHER NAME: " + teacherName);
        
        if (teacherId == null) {
            System.out.println("⚠️ Teacher ID is null, redirecting to login");
            return "redirect:/login";
        }
        
        List<Course> allTeacherCourses = courseService.getCoursesByTeacherId(teacherId);
        
        System.out.println("📊 Teacher " + teacherId + " has " + allTeacherCourses.size() + " courses");
        
        List<Course> pendingCourses = new ArrayList<>();
        List<Course> validatedCourses = new ArrayList<>();
        List<Course> publishedCourses = new ArrayList<>();
        
        for (Course course : allTeacherCourses) {
            String status = course.getStatus() != null ? course.getStatus() : "UNKNOWN";
            
            System.out.println("   - Course: " + course.getTitle() + " | status: " + status + " | ID: " + course.getId());
            
            switch (status) {
                case "PENDING":
                    pendingCourses.add(course);
                    break;
                case "VALIDATED":
                    validatedCourses.add(course);
                    break;
                case "PUBLISHED":
                case "ACTIVE":
                    publishedCourses.add(course);
                    break;
                default:
                    pendingCourses.add(course);
                    break;
            }
            
            long quizCount = quizService.countQuizzesByCourse(course.getId());
            course.setQuizCount((int) quizCount);
            
            if ("PUBLISHED".equals(status) || "ACTIVE".equals(status)) {
                long studentCount = enrollmentService.countStudentsByCourse(course.getId());
                course.setTotalStudents((int) studentCount);
            }
        }
        
        System.out.println("📊 Summary - Pending: " + pendingCourses.size() + 
                          ", Validated: " + validatedCourses.size() + 
                          ", Published: " + publishedCourses.size());
        
        model.addAttribute("teacherId", teacherId);
        model.addAttribute("teacherName", teacherName != null ? teacherName : "Professor");
        model.addAttribute("teacherEmail", teacherEmail != null ? teacherEmail : "");
        model.addAttribute("pendingCourses", pendingCourses);
        model.addAttribute("validatedCourses", validatedCourses);
        model.addAttribute("publishedCourses", publishedCourses);
        model.addAttribute("totalCourses", allTeacherCourses.size());
        
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
    
    // ==================== GESTION DES QUIZ (API) ====================
    
    @PostMapping("/api/create-quiz")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createQuiz(@RequestBody Map<String, Object> quizData, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            String teacherName = (String) session.getAttribute("teacherName");
            
            System.out.println("=== CRÉATION QUIZ DEPUIS DASHBOARD ===");
            System.out.println("Teacher ID: " + teacherId);
            System.out.println("Quiz Data reçu: " + quizData);
            
            if (teacherId == null) {
                response.put("success", false);
                response.put("message", "Session expirée - Veuillez vous reconnecter");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            Object courseIdObj = quizData.get("courseId");
            if (courseIdObj == null) {
                response.put("success", false);
                response.put("message", "ID du cours manquant");
                return ResponseEntity.badRequest().body(response);
            }
            
            Long courseId = Long.valueOf(courseIdObj.toString());
            String title = (String) quizData.get("title");
            
            if (title == null || title.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Le titre du quiz est requis");
                return ResponseEntity.badRequest().body(response);
            }
            
            String description = (String) quizData.getOrDefault("description", "");
            Integer timeLimit = quizData.get("timeLimit") != null ? Integer.valueOf(quizData.get("timeLimit").toString()) : 30;
            Integer passingScore = quizData.get("passingScore") != null ? Integer.valueOf(quizData.get("passingScore").toString()) : 70;
            String courseModule = (String) quizData.getOrDefault("courseModule", "");
            String courseNiveau = (String) quizData.getOrDefault("courseNiveau", "");
            
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
            
            System.out.println("✅ QUIZ CRÉÉ AVEC SUCCÈS - ID: " + savedQuiz.getId());
            
            response.put("success", true);
            response.put("quizId", savedQuiz.getId());
            response.put("message", "Quiz créé avec succès !");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    // ⭐⭐⭐ MÉTHODE AJOUTÉE POUR LES QUESTIONS ⭐⭐⭐
    @PostMapping("/api/quizzes/{quizId}/questions")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addQuestionToQuiz(
            @PathVariable Long quizId,
            @RequestBody Map<String, Object> questionData,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            
            System.out.println("=== AJOUT QUESTION AU QUIZ ===");
            System.out.println("Quiz ID: " + quizId);
            System.out.println("Teacher ID: " + teacherId);
            System.out.println("Question Data: " + questionData);
            
            if (teacherId == null) {
                response.put("success", false);
                response.put("message", "Session expirée");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            Quiz quiz = quizService.getQuizById(quizId);
            if (quiz == null || !quiz.getTeacherId().equals(teacherId)) {
                response.put("success", false);
                response.put("message", "Quiz non trouvé ou accès non autorisé");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            String questionText = (String) questionData.get("text");
            if (questionText == null || questionText.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Le texte de la question est requis");
                return ResponseEntity.badRequest().body(response);
            }
            
            Question question = new Question();
            question.setQuestionText(questionText);
            question.setPoints(questionData.get("points") != null ? Integer.valueOf(questionData.get("points").toString()) : 10);
            question.setQuestionType(questionData.get("type") != null ? questionData.get("type").toString() : "SINGLE_CHOICE");
            question.setOrderNumber(questionData.get("order") != null ? Integer.valueOf(questionData.get("order").toString()) : 1);
            
            @SuppressWarnings("unchecked")
            List<String> options = (List<String>) questionData.get("options");
            if (options != null && !options.isEmpty()) {
                question.setOptions(options);
            }
            
            Object correctAnswerObj = questionData.get("correctAnswer");
            if (correctAnswerObj != null) {
                question.setCorrectAnswer(Integer.valueOf(correctAnswerObj.toString()));
            }
            
            Question savedQuestion = quizService.addQuestionToQuiz(quizId, question);
            
            System.out.println("✅ Question ajoutée avec succès - ID: " + savedQuestion.getId());
            
            response.put("success", true);
            response.put("message", "Question ajoutée avec succès");
            response.put("questionId", savedQuestion.getId());
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/api/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTeacherStats(HttpSession session) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            
            System.out.println("=== RÉCUPÉRATION STATISTIQUES ===");
            System.out.println("Teacher ID: " + teacherId);
            
            if (teacherId == null) {
                stats.put("success", false);
                stats.put("message", "Session expirée");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(stats);
            }
            
            List<Course> courses = courseService.getCoursesByTeacherId(teacherId);
            List<Quiz> quizzes = quizService.getQuizzesByTeacher(teacherId);
            long totalStudents = enrollmentService.countTotalStudentsByTeacher(teacherId);
            
            stats.put("success", true);
            stats.put("totalCourses", courses.size());
            stats.put("totalQuizzes", quizzes.size());
            stats.put("totalStudents", totalStudents);
            
            System.out.println("Stats - Cours: " + courses.size() + ", Quiz: " + quizzes.size() + ", Étudiants: " + totalStudents);
            
        } catch (Exception e) {
            e.printStackTrace();
            stats.put("success", false);
            stats.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(stats);
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
    
    @GetMapping("/courses/niveau/{niveau}")
    @ResponseBody
    public ResponseEntity<List<Course>> getCoursesByNiveau(@PathVariable String niveau) {
        if (niveau == null || niveau.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Course> courses = courseService.findByNiveau(niveau);
        return ResponseEntity.ok(courses != null ? courses : List.of());
    }
    
    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
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