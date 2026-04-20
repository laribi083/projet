package com.votredomaine.modelememoire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.votredomaine.modelememoire.model.*;
import com.votredomaine.modelememoire.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class QuizController {
    
    @Autowired
    private QuizService quizService;
    
    @Autowired
    private EnrollmentService enrollmentService;
    
    @Autowired
    private Courseservice courseService;
    
    @Autowired
    private QuizResultService quizResultService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // ==================== PARTIE ENSEIGNANT ====================
    
    @GetMapping("/teacher/create-quiz")
    public String showCreateQuizFormTeacher(@RequestParam Long courseId, 
                                             @RequestParam String courseModule, 
                                             @RequestParam String courseNiveau,
                                             Model model) {
        model.addAttribute("courseId", courseId);
        model.addAttribute("courseModule", courseModule);
        model.addAttribute("courseNiveau", courseNiveau);
        return "htmlTeacher/create-quiz";
    }
    
    @GetMapping("/teacher/course/{courseId}/quizzes")
    public String getCourseQuizzes(@PathVariable Long courseId, Model model, HttpSession session) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        if (teacherId == null) {
            return "redirect:/login";
        }
        List<Quiz> quizzes = quizService.getQuizzesByCourse(courseId);
        model.addAttribute("quizzes", quizzes);
        model.addAttribute("courseId", courseId);
        return "htmlTeacher/quiz-list";
    }
    
    @GetMapping("/teacher/quiz/{quizId}/questions")
    public String getQuizQuestions(@PathVariable Long quizId, Model model, HttpSession session) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        if (teacherId == null) {
            return "redirect:/login";
        }
        
        Quiz quiz = quizService.getQuizById(quizId);
        if (quiz == null || !quiz.getTeacherId().equals(teacherId)) {
            return "redirect:/teacher/dashboard";
        }
        
        List<Question> questions = quizService.getQuestionsByQuizId(quizId);
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questions);
        return "htmlTeacher/quiz-questions";
    }
    
    @PostMapping("/teacher/api/quizzes/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createQuiz(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("courseId") Long courseId,
            @RequestParam("courseModule") String courseModule,
            @RequestParam("courseNiveau") String courseNiveau,
            @RequestParam("durationMinutes") Integer durationMinutes,
            @RequestParam("passingScore") Integer passingScore,
            @RequestParam(value = "questionsData", required = false) String questionsData,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            String teacherName = (String) session.getAttribute("teacherName");
            
            if (teacherId == null) {
                response.put("success", false);
                response.put("message", "Session expirée");
                return ResponseEntity.ok(response);
            }
            
            if (title == null || title.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Le titre du quiz est obligatoire");
                return ResponseEntity.ok(response);
            }
            
            if (courseId == null) {
                response.put("success", false);
                response.put("message", "ID du cours manquant");
                return ResponseEntity.ok(response);
            }
            
            Quiz quiz = new Quiz();
            quiz.setTitle(title);
            quiz.setDescription(description != null ? description : "");
            quiz.setCourseId(courseId);
            quiz.setTeacherId(teacherId);
            quiz.setTeacherName(teacherName != null ? teacherName : "Professeur");
            quiz.setModule(courseModule != null ? courseModule : "");
            quiz.setNiveau(courseNiveau != null ? courseNiveau : "");
            quiz.setTimeLimit(durationMinutes != null ? durationMinutes : 30);
            quiz.setPassingScore(passingScore != null ? passingScore : 70);
            quiz.setStatus("ACTIVE");
            quiz.setCreatedAt(LocalDateTime.now());
            quiz.setUpdatedAt(LocalDateTime.now());
            quiz.setTotalQuestions(0);
            
            List<Question> questions = new ArrayList<>();
            if (questionsData != null && !questionsData.trim().isEmpty()) {
                try {
                    List<Map<String, Object>> questionsList = objectMapper.readValue(
                        questionsData, 
                        new TypeReference<List<Map<String, Object>>>() {}
                    );
                    
                    int order = 1;
                    for (Map<String, Object> qData : questionsList) {
                        Question question = new Question();
                        String questionText = (String) qData.get("text");
                        if (questionText == null) {
                            questionText = (String) qData.get("questionText");
                        }
                        question.setQuestionText(questionText != null ? questionText : "Question sans texte");
                        Object pointsObj = qData.get("points");
                        question.setPoints(pointsObj != null ? Integer.valueOf(pointsObj.toString()) : 1);
                        question.setOrderNumber(order++);
                        questions.add(question);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du parsing des questions: " + e.getMessage());
                }
            }
            
            Quiz savedQuiz = quizService.createQuiz(quiz, questions);
            
            response.put("success", true);
            response.put("message", "Quiz créé avec succès !");
            response.put("quizId", savedQuiz.getId());
            response.put("totalQuestions", savedQuiz.getTotalQuestions());
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== API POUR ENSEIGNANT ====================
    
    @GetMapping("/teacher/api/quizzes")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTeacherQuizzes(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            
            System.out.println("=== RÉCUPÉRATION DES QUIZ ENSEIGNANT ===");
            System.out.println("Teacher ID: " + teacherId);
            
            if (teacherId == null) {
                response.put("success", false);
                response.put("message", "Session expirée");
                return ResponseEntity.status(401).body(response);
            }
            
            List<Quiz> quizzes = quizService.getQuizzesByTeacher(teacherId);
            System.out.println("Nombre de quiz trouvés: " + quizzes.size());
            
            // Créer une copie simple sans les questions pour éviter la boucle infinie
            List<Map<String, Object>> quizList = new ArrayList<>();
            for (Quiz quiz : quizzes) {
                Map<String, Object> quizMap = new HashMap<>();
                quizMap.put("id", quiz.getId());
                quizMap.put("title", quiz.getTitle());
                quizMap.put("description", quiz.getDescription());
                quizMap.put("courseId", quiz.getCourseId());
                quizMap.put("teacherId", quiz.getTeacherId());
                quizMap.put("teacherName", quiz.getTeacherName());
                quizMap.put("module", quiz.getModule());
                quizMap.put("niveau", quiz.getNiveau());
                quizMap.put("timeLimit", quiz.getTimeLimit());
                quizMap.put("passingScore", quiz.getPassingScore());
                quizMap.put("status", quiz.getStatus());
                quizMap.put("totalQuestions", quiz.getTotalQuestions());
                quizMap.put("createdAt", quiz.getCreatedAt());
                
                Course course = courseService.getCourseById(quiz.getCourseId());
                quizMap.put("courseTitle", course != null ? course.getTitle() : "N/A");
                
                quizList.add(quizMap);
            }
            
            response.put("success", true);
            response.put("quizzes", quizList);
            response.put("count", quizList.size());
            
            System.out.println("Quiz envoyés: " + quizList.size());
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/teacher/api/delete-quiz/{quizId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteQuiz(@PathVariable Long quizId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            
            if (teacherId == null) {
                response.put("success", false);
                response.put("message", "Session expirée");
                return ResponseEntity.status(401).body(response);
            }
            
            Quiz quiz = quizService.getQuizById(quizId);
            if (quiz == null || !quiz.getTeacherId().equals(teacherId)) {
                response.put("success", false);
                response.put("message", "Quiz non trouvé");
                return ResponseEntity.status(404).body(response);
            }
            
            quizService.deleteQuiz(quizId);
            
            response.put("success", true);
            response.put("message", "Quiz supprimé avec succès");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== PARTIE ÉTUDIANT ====================
    
    @GetMapping("/student/quizzes")
    public String showStudentQuizzes(Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        Long userId = (Long) session.getAttribute("userId");
        String niveau = (String) session.getAttribute("niveau");
        
        System.out.println("=== AFFICHAGE DES QUIZ ÉTUDIANT ===");
        System.out.println("Étudiant: " + userName + " (ID: " + userId + ")");
        
        if (userName == null || userId == null) {
            return "redirect:/login";
        }
        
        List<Quiz> allActiveQuizzes = quizService.getAllActiveQuizzes();
        System.out.println("Total quiz actifs: " + allActiveQuizzes.size());
        
        List<Long> enrolledCourseIds = enrollmentService.getDownloadedCourseIds(userId);
        System.out.println("Cours inscrits (IDs): " + enrolledCourseIds);
        
        List<Quiz> availableQuizzes = allActiveQuizzes.stream()
            .filter(quiz -> enrolledCourseIds.contains(quiz.getCourseId()))
            .collect(Collectors.toList());
        System.out.println("Quiz disponibles après filtrage: " + availableQuizzes.size());
        
        List<Long> completedQuizIds = quizResultService.findCompletedQuizIdsByStudentId(userId);
        System.out.println("Quiz déjà complétés: " + completedQuizIds);
        
        for (Quiz quiz : availableQuizzes) {
            Course course = courseService.getCourseById(quiz.getCourseId());
            if (course != null) {
                quiz.setCourseTitle(course.getTitle());
            }
        }
        
        model.addAttribute("quizzes", availableQuizzes);
        model.addAttribute("completedQuizIds", completedQuizIds);
        model.addAttribute("userName", userName);
        model.addAttribute("niveau", niveau);
        
        return "htmlstudent/quizzes";
    }
    
    @GetMapping("/quiz/take/{quizId}")
    public String takeQuiz(@PathVariable Long quizId, Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        Long userId = (Long) session.getAttribute("userId");
        
        if (userName == null || userId == null) {
            return "redirect:/login";
        }
        
        Quiz quiz = quizService.getQuizById(quizId);
        if (quiz == null || !"ACTIVE".equals(quiz.getStatus())) {
            return "redirect:/student/dashboard";
        }
        
        List<Long> enrolledCourseIds = enrollmentService.getDownloadedCourseIds(userId);
        boolean hasAccess = enrolledCourseIds.contains(quiz.getCourseId());
        
        if (!hasAccess) {
            return "redirect:/student/dashboard";
        }
        
        boolean alreadyCompleted = quizResultService.hasStudentCompletedQuiz(userId, quizId);
        if (alreadyCompleted) {
            return "redirect:/quiz/result/" + quizId;
        }
        
        List<Question> questions = quizService.getQuestionsByQuizId(quizId);
        
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questions);
        model.addAttribute("totalQuestions", questions.size());
        model.addAttribute("userName", userName);
        
        return "htmlstudent/take-quiz";
    }
    
    @PostMapping("/quiz/submit/{quizId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> submitQuiz(
            @PathVariable Long quizId,
            @RequestBody Map<String, Object> submission,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String userName = (String) session.getAttribute("userName");
            Long userId = (Long) session.getAttribute("userId");
            
            if (userName == null || userId == null) {
                response.put("success", false);
                response.put("message", "Session expirée");
                return ResponseEntity.ok(response);
            }
            
            Quiz quiz = quizService.getQuizById(quizId);
            if (quiz == null) {
                response.put("success", false);
                response.put("message", "Quiz non trouvé");
                return ResponseEntity.ok(response);
            }
            
            if (quizResultService.hasStudentCompletedQuiz(userId, quizId)) {
                response.put("success", false);
                response.put("message", "Vous avez déjà complété ce quiz");
                return ResponseEntity.ok(response);
            }
            
            List<Question> questions = quizService.getQuestionsByQuizId(quizId);
            @SuppressWarnings("unchecked")
            List<Integer> userAnswers = (List<Integer>) submission.get("answers");
            
            int score = 0;
            int totalPoints = 0;
            
            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                int points = q.getPoints() != null ? q.getPoints() : 1;
                totalPoints += points;
                
                if (userAnswers != null && i < userAnswers.size() && userAnswers.get(i) != null) {
                    Integer correctAnswer = q.getCorrectAnswer();
                    if (correctAnswer != null && userAnswers.get(i).equals(correctAnswer)) {
                        score += points;
                    }
                }
            }
            
            int percentage = totalPoints > 0 ? (score * 100) / totalPoints : 0;
            Integer passingScore = quiz.getPassingScore() != null ? quiz.getPassingScore() : 70;
            boolean passed = percentage >= passingScore;
            
            QuizResult result = new QuizResult();
            result.setQuizId(quizId);
            result.setQuizTitle(quiz.getTitle());
            result.setStudentId(userId);
            result.setStudentName(userName);
            result.setScore(score);
            result.setTotalPoints(totalPoints);
            result.setPercentage(percentage);
            result.setPassed(passed);
            result.setCompletedAt(LocalDateTime.now());
            quizResultService.saveResult(result);
            
            response.put("success", true);
            response.put("score", score);
            response.put("totalPoints", totalPoints);
            response.put("percentage", percentage);
            response.put("passed", passed);
            response.put("passingScore", passingScore);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/quiz/result/{quizId}")
    public String showResult(@PathVariable Long quizId, Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        Long userId = (Long) session.getAttribute("userId");
        
        if (userName == null || userId == null) {
            return "redirect:/login";
        }
        
        Quiz quiz = quizService.getQuizById(quizId);
        QuizResult result = quizResultService.getResultByStudentAndQuiz(userId, quizId);
        
        if (result == null) {
            return "redirect:/student/quizzes";
        }
        
        model.addAttribute("quiz", quiz);
        model.addAttribute("result", result);
        model.addAttribute("userName", userName);
        
        return "htmlstudent/quiz-result";
    }
    
    @GetMapping("/quiz/preview/{quizId}")
    public String previewQuiz(@PathVariable Long quizId, Model model, HttpSession session) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        if (teacherId == null) {
            return "redirect:/login";
        }
        
        Quiz quiz = quizService.getQuizById(quizId);
        if (quiz == null || !quiz.getTeacherId().equals(teacherId)) {
            return "redirect:/teacher/dashboard";
        }
        
        List<Question> questions = quizService.getQuestionsByQuizId(quizId);
        
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questions);
        model.addAttribute("totalQuestions", questions.size());
        model.addAttribute("preview", true);
        model.addAttribute("userName", session.getAttribute("teacherName"));
        
        return "htmlstudent/take-quiz";
    }
    @GetMapping("/debug/check-quizzes")
@ResponseBody
public Map<String, Object> debugCheckQuizzes(HttpSession session) {
    Map<String, Object> result = new HashMap<>();
    
    try {
        Long teacherId = (Long) session.getAttribute("teacherId");
        result.put("teacherId", teacherId);
        result.put("sessionExists", teacherId != null);
        
        if (teacherId != null) {
            List<Quiz> quizzes = quizService.getQuizzesByTeacher(teacherId);
            result.put("quizzesCount", quizzes.size());
            
            List<Map<String, Object>> quizList = new ArrayList<>();
            for (Quiz quiz : quizzes) {
                Map<String, Object> q = new HashMap<>();
                q.put("id", quiz.getId());
                q.put("title", quiz.getTitle());
                q.put("courseId", quiz.getCourseId());
                quizList.add(q);
            }
            result.put("quizzes", quizList);
        }
        
        result.put("success", true);
        
    } catch (Exception e) {
        result.put("success", false);
        result.put("error", e.getMessage());
        e.printStackTrace();
    }
    
    return result;
}
}