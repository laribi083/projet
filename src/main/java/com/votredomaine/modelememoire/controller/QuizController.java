package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Question;
import com.votredomaine.modelememoire.model.Quiz;
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

@Controller
@RequestMapping("/teacher")
public class QuizController {
    
    @Autowired
    private QuizService quizService;
    
    // Afficher la liste des quiz pour un cours
    @GetMapping("/course/{courseId}/quizzes")
    public String getCourseQuizzes(@PathVariable Long courseId, Model model, HttpSession session) {
        List<Quiz> quizzes = quizService.getQuizzesByCourse(courseId);
        model.addAttribute("quizzes", quizzes);
        model.addAttribute("courseId", courseId);
        return "htmlTeacher/quiz-list";
    }
    
    // Afficher le formulaire de création de quiz
    @GetMapping("/create-quiz")
    public String showCreateQuizForm(Model model, @RequestParam Long courseId, 
                                      @RequestParam String courseModule, 
                                      @RequestParam String courseNiveau) {
        model.addAttribute("courseId", courseId);
        model.addAttribute("courseModule", courseModule);
        model.addAttribute("courseNiveau", courseNiveau);
        return "htmlTeacher/create-quiz";
    }
    
    // Créer un quiz
    @PostMapping("/api/quizzes")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createQuiz(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("courseId") Long courseId,
            @RequestParam("courseModule") String courseModule,
            @RequestParam("courseNiveau") String courseNiveau,
            @RequestParam("durationMinutes") Integer durationMinutes,
            @RequestParam("passingScore") Integer passingScore,
            @RequestParam("questions") String questionsJson,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long teacherId = (Long) session.getAttribute("teacherId");
            String teacherName = (String) session.getAttribute("teacherName");
            
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
            
            // Parse questions from JSON
            // Implementation depends on your frontend structure
            
            response.put("success", true);
            response.put("message", "Quiz créé avec succès");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}