package com.votredomaine.modelememoire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.votredomaine.modelememoire.model.Question;
import com.votredomaine.modelememoire.model.Quiz;
import com.votredomaine.modelememoire.service.QuizService;
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
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // ==================== PARTIE TEACHER ====================
    
    /**
     * Affiche le formulaire de création de quiz pour un enseignant
     */
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
    
    /**
     * Affiche la liste des quiz d'un cours pour un enseignant
     */
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
    
    /**
     * Affiche les questions d'un quiz pour un enseignant
     */
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
    
    // ==================== API QUIZ (TEACHER) ====================
    
    /**
     * API pour créer un quiz complet avec ses questions
     */
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
            
            // Validation des champs obligatoires
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
    
    /**
     * API pour récupérer un quiz par son ID
     */
    @GetMapping("/teacher/api/quizzes/{quizId}")
    @ResponseBody
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long quizId, HttpSession session) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        if (teacherId == null) {
            return ResponseEntity.status(401).build();
        }
        
        Quiz quiz = quizService.getQuizById(quizId);
        if (quiz == null || !quiz.getTeacherId().equals(teacherId)) {
            return ResponseEntity.status(404).build();
        }
        
        return ResponseEntity.ok(quiz);
    }
    
    /**
     * API pour récupérer les questions d'un quiz
     */
    @GetMapping("/teacher/api/quizzes/{quizId}/questions")
    @ResponseBody
    public ResponseEntity<List<Question>> getQuizQuestionsApi(@PathVariable Long quizId, HttpSession session) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        if (teacherId == null) {
            return ResponseEntity.status(401).build();
        }
        
        Quiz quiz = quizService.getQuizById(quizId);
        if (quiz == null || !quiz.getTeacherId().equals(teacherId)) {
            return ResponseEntity.status(404).build();
        }
        
        List<Question> questions = quizService.getQuestionsByQuizId(quizId);
        return ResponseEntity.ok(questions);
    }
    
    /**
     * API pour ajouter une question à un quiz
     */
    @PostMapping("/teacher/api/quizzes/{quizId}/questions")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addQuestionToQuiz(
            @PathVariable Long quizId,
            @RequestBody Map<String, Object> questionData,
            HttpSession session) {
        
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
            
            String questionText = (String) questionData.get("questionText");
            if (questionText == null || questionText.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Le texte de la question est obligatoire");
                return ResponseEntity.badRequest().body(response);
            }
            
            Question question = new Question();
            question.setQuestionText(questionText);
            question.setPoints(questionData.get("points") != null ? Integer.valueOf(questionData.get("points").toString()) : 10);
            question.setQuestionType(questionData.get("questionType") != null ? questionData.get("questionType").toString() : "SINGLE_CHOICE");
            question.setOrderNumber(questionData.get("orderNumber") != null ? Integer.valueOf(questionData.get("orderNumber").toString()) : 1);
            
            @SuppressWarnings("unchecked")
            List<String> options = (List<String>) questionData.get("options");
            if (options != null) {
                question.setOptions(options);
            }
            
            Object correctAnswerObj = questionData.get("correctAnswer");
            if (correctAnswerObj != null) {
                question.setCorrectAnswer(Integer.valueOf(correctAnswerObj.toString()));
            }
            
            Question savedQuestion = quizService.addQuestionToQuiz(quizId, question);
            
            response.put("success", true);
            response.put("message", "Question ajoutée avec succès");
            response.put("questionId", savedQuestion.getId());
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * API pour supprimer un quiz
     */
    @DeleteMapping("/teacher/api/quizzes/{quizId}")
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
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * API pour supprimer une question d'un quiz
     */
    @DeleteMapping("/teacher/api/quizzes/{quizId}/questions/{questionId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteQuestion(@PathVariable Long quizId, @PathVariable Long questionId, HttpSession session) {
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
            
            quizService.deleteQuestion(questionId);
            response.put("success", true);
            response.put("message", "Question supprimée avec succès");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // ==================== PARTIE STUDENT ====================
    
    /**
     * Affiche la page avec tous les quiz disponibles pour l'étudiant
     */
    @GetMapping("/student/quizzes")
    public String showStudentQuizzes(Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        Long userId = (Long) session.getAttribute("userId");
        String niveau = (String) session.getAttribute("niveau");
        
        if (userName == null || userId == null) {
            return "redirect:/login";
        }
        
        // Récupérer tous les quiz actifs
        List<Quiz> allQuizzes = quizService.getAllActiveQuizzes();
        
        // Filtrer par niveau si l'étudiant a un niveau
        if (niveau != null && !niveau.isEmpty()) {
            allQuizzes = allQuizzes.stream()
                .filter(q -> q.getNiveau() != null && q.getNiveau().equals(niveau))
                .collect(Collectors.toList());
        }
        
        model.addAttribute("quizzes", allQuizzes);
        model.addAttribute("userName", userName);
        model.addAttribute("niveau", niveau);
        
        return "htmlstudent/quizzes";
    }
    
    /**
     * Affiche la page pour passer un quiz
     */
    @GetMapping("/quiz/take/{quizId}")
    public String takeQuiz(@PathVariable Long quizId, Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        if (userName == null) {
            return "redirect:/login";
        }
        
        Quiz quiz = quizService.getQuizById(quizId);
        if (quiz == null || !"ACTIVE".equals(quiz.getStatus())) {
            return "redirect:/student/dashboard";
        }
        
        List<Question> questions = quizService.getQuestionsByQuizId(quizId);
        
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questions);
        model.addAttribute("totalQuestions", questions.size());
        model.addAttribute("userName", userName);
        
        return "htmlstudent/take-quiz";
    }
    
    /**
     * API pour soumettre les réponses d'un quiz
     */
    @PostMapping("/quiz/submit/{quizId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> submitQuiz(
            @PathVariable Long quizId,
            @RequestBody Map<String, Object> submission,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String userName = (String) session.getAttribute("userName");
            if (userName == null) {
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
            
            List<Question> questions = quizService.getQuestionsByQuizId(quizId);
            @SuppressWarnings("unchecked")
            List<Integer> userAnswers = (List<Integer>) submission.get("answers");
            
            int score = 0;
            int totalPoints = 0;
            
            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                int points = q.getPoints() != null ? q.getPoints() : 1;
                totalPoints += points;
                
                if (userAnswers != null && i < userAnswers.size() && 
                    userAnswers.get(i) != null) {
                    Integer correctAnswer = q.getCorrectAnswer();
                    if (correctAnswer != null && userAnswers.get(i).equals(correctAnswer)) {
                        score += points;
                    }
                }
            }
            
            int percentage = totalPoints > 0 ? (score * 100) / totalPoints : 0;
            Integer passingScore = quiz.getPassingScore() != null ? quiz.getPassingScore() : 70;
            boolean passed = percentage >= passingScore;
            
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
    
    /**
     * Affiche la page des résultats d'un quiz
     */
    @GetMapping("/quiz/result/{quizId}")
    public String showResult(@PathVariable Long quizId, Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        if (userName == null) {
            return "redirect:/login";
        }
        
        Quiz quiz = quizService.getQuizById(quizId);
        model.addAttribute("quiz", quiz);
        model.addAttribute("userName", userName);
        return "htmlstudent/quiz-result";
    }
    
    /**
     * Aperçu d'un quiz pour l'enseignant (prévisualisation)
     */
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
}