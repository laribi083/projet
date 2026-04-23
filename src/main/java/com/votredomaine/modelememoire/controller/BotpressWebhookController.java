// BotpressWebhookController.java
package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.BotpressRequest;
import com.votredomaine.modelememoire.model.BotpressResponse;
import com.votredomaine.modelememoire.service.BotpressDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/botpress")
public class BotpressWebhookController {
    
    @Autowired
    private BotpressDataService dataService;
    
    /**
     * Point d'entrée principal pour Botpress (webhook)
     */
    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> handleWebhook(
            @RequestBody BotpressRequest request,
            HttpSession session) {
        
        Long userId = request.getUserId();
        String message = request.getMessage();
        String intent = request.getIntent();
        
        // Si userId est null, essayer de le récupérer de la session
        if (userId == null || userId == 0) {
            userId = (Long) session.getAttribute("userId");
        }
        
        if (userId == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("text", "❌ Je n'arrive pas à vous identifier. Veuillez vous reconnecter.");
            return ResponseEntity.ok(errorResponse);
        }
        
        String answer = processIntent(userId, message, intent);
        
        Map<String, Object> response = new HashMap<>();
        response.put("text", answer);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Routeur d'intents
     */
    private String processIntent(Long userId, String message, String intent) {
        String lowerMessage = message.toLowerCase();
        
        // 1. SALUTATION
        if ("greeting".equals(intent) || containsAny(lowerMessage, "bonjour", "salut", "hello")) {
            return dataService.getWelcomeMessage(userId);
        }
        
        // 2. COURS
        if ("ask_courses".equals(intent) || containsAny(lowerMessage, "cours", "mes cours", "leçon")) {
            return dataService.getStudentCourses(userId);
        }
        
        // 3. NOTES / SCORES
        if ("ask_grades".equals(intent) || containsAny(lowerMessage, "note", "score", "moyenne", "résultat")) {
            return dataService.getStudentGrades(userId);
        }
        
        // 4. PROGRESSION
        if ("ask_progress".equals(intent) || containsAny(lowerMessage, "progres", "avancement", "où j'en suis")) {
            return dataService.getStudentProgress(userId);
        }
        
        // 5. QUIZ
        if ("ask_quiz".equals(intent) || containsAny(lowerMessage, "quiz", "exercice", "test")) {
            return dataService.getAvailableQuizzes(userId);
        }
        
        // 6. CERTIFICAT
        if ("ask_certificate".equals(intent) || containsAny(lowerMessage, "certificat", "attestation", "diplôme")) {
            return dataService.getCertificateInfo(userId);
        }
        
        // 7. ENSEIGNANT
        if ("ask_teacher".equals(intent) || containsAny(lowerMessage, "prof", "enseignant", "contacter")) {
            return dataService.getTeacherContactInfo();
        }
        
        // 8. STATISTIQUES GÉNÉRALES
        if ("ask_stats".equals(intent) || containsAny(lowerMessage, "statistique", "combien", "plateforme")) {
            return dataService.getGeneralStats();
        }
        
        // 9. DÉTAILS D'UN COURS SPÉCIFIQUE
        if (containsAny(lowerMessage, "détail", "contenu", "information")) {
            String courseName = extractCourseName(message);
            if (courseName != null) {
                return dataService.getCourseDetails(courseName);
            }
        }
        
        // 10. AU REVOIR
        if ("goodbye".equals(intent) || containsAny(lowerMessage, "au revoir", "bye", "à plus")) {
            return "Au revoir ! 👋 N'hésitez pas à revenir si vous avez d'autres questions. Bonne continuation sur BrainLearning !";
        }
        
        // Réponse par défaut (le fallback sera géré par Botpress)
        return null;
    }
    
    /**
     * Vérifie si le message contient l'un des mots-clés
     */
    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Extrait le nom d'un cours d'un message
     */
    private String extractCourseName(String message) {
        String lowerMessage = message.toLowerCase();
        String[] words = message.split(" ");
        
        for (int i = 0; i < words.length - 1; i++) {
            if (words[i].equalsIgnoreCase("cours") || words[i].equalsIgnoreCase("course")) {
                StringBuilder courseName = new StringBuilder();
                for (int j = i + 1; j < words.length; j++) {
                    courseName.append(words[j]).append(" ");
                }
                return courseName.toString().trim();
            }
        }
        return null;
    }
    
    /**
     * Endpoint de test pour vérifier que le webhook fonctionne
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "active");
        response.put("message", "Botpress webhook is running");
        return ResponseEntity.ok(response);
    }
}