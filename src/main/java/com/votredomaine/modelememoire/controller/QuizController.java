package com.votredomaine.modelememoire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

@Controller
@RequestMapping("/quiz")
public class QuizController {
    
    @Autowired
    private QuizService quizService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // ========== PARTIE TEACHER ==========
    
    /**
     * API: Créer un quiz (appel AJAX)
     */
    @PostMapping("/api/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createQuiz(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("courseId") Long courseId,
            @RequestParam("courseModule") String courseModule,
            @RequestParam("courseNiveau") String courseNiveau,
            @RequestParam("durationMinutes") Integer durationMinutes,
            @RequestParam("passingScore") Integer passingScore,
            @RequestParam("questionsData") String questionsData,
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
            
            Quiz quiz = new Quiz();
            quiz.setTitle(title);
            quiz.setDescription(description);
            quiz.setCourseId(courseId);
            quiz.setCourseModule(courseModule);
            quiz.setCourseNiveau(courseNiveau);
            quiz.setTeacherId(teacherId);
            quiz.setTeacherName(teacherName);
            quiz.setDurationMinutes(durationMinutes);
            quiz.setPassingScore(passingScore);
            quiz.setStatus("ACTIVE");
            quiz.setCreatedAt(LocalDateTime.now());
            quiz.setUpdatedAt(LocalDateTime.now());
            
            List<Map<String, Object>> questionsList = objectMapper.readValue(
                questionsData, 
                new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {}
            );
            
            List<Question> questions = new ArrayList<>();
            for (Map<String, Object> qData : questionsList) {
                Question question = new Question();
                question.setText((String) qData.get("text"));
                question.setOptions((List<String>) qData.get("options"));
                question.setCorrectAnswer((Integer) qData.get("correctAnswer"));
                question.setPoints(1);
                questions.add(question);
            }
            
            Quiz savedQuiz = quizService.createQuiz(quiz, questions);
            
            response.put("success", true);
            response.put("message", "Quiz créé avec succès !");
            response.put("quizId", savedQuiz.getId());
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Supprimer un quiz (API)
     */
    @DeleteMapping("/api/delete/{quizId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteQuiz(@PathVariable Long quizId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            Quiz quiz = quizService.getQuizById(quizId);
            
            if (quiz == null) {
                response.put("success", false);
                response.put("message", "Quiz non trouvé");
                return ResponseEntity.ok(response);
            }
            
            if (!quiz.getTeacherId().equals(teacherId)) {
                response.put("success", false);
                response.put("message", "Accès non autorisé");
                return ResponseEntity.ok(response);
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
    
    /**
     * Prévisualiser un quiz (pour Teacher)
     */
    @GetMapping("/preview/{quizId}")
    public String previewQuiz(@PathVariable Long quizId, Model model, HttpSession session) {
        Long teacherId = (Long) session.getAttribute("teacherId");
        if (teacherId == null) {
            return "redirect:/login";
        }
        
        Quiz quiz = quizService.getQuizById(quizId);
        if (quiz == null) {
            return "redirect:/teacher/dashboard";
        }
        
        List<Question> questions = quizService.getQuestionsByQuizId(quizId);
        
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questions);
        model.addAttribute("totalQuestions", questions.size());
        model.addAttribute("preview", true);
        
        return "htmlstudent/take-quiz";
    }
    
    // ========== PARTIE STUDENT ==========
    
    /**
     * Affiche la page pour passer un quiz
     */
    @GetMapping("/take/{quizId}")
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
        
        return "htmlstudent/take-quiz";
    }
    
    /**
     * Soumettre les réponses d'un quiz
     */
    @PostMapping("/submit/{quizId}")
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
            List<Integer> userAnswers = (List<Integer>) submission.get("answers");
            
            int score = 0;
            int totalPoints = 0;
            
            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                totalPoints += q.getPoints();
                
                if (userAnswers != null && i < userAnswers.size() && 
                    userAnswers.get(i) != null && 
                    userAnswers.get(i).equals(q.getCorrectAnswer())) {
                    score += q.getPoints();
                }
            }
            
            int percentage = totalPoints > 0 ? (score * 100) / totalPoints : 0;
            boolean passed = percentage >= quiz.getPassingScore();
            
            response.put("success", true);
            response.put("score", score);
            response.put("totalPoints", totalPoints);
            response.put("percentage", percentage);
            response.put("passed", passed);
            response.put("passingScore", quiz.getPassingScore());
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Affiche le résultat d'un quiz
     */
    @GetMapping("/result/{quizId}")
    public String showResult(@PathVariable Long quizId, Model model, HttpSession session) {
        Quiz quiz = quizService.getQuizById(quizId);
        model.addAttribute("quiz", quiz);
        return "htmlstudent/quiz-result";
    }
}