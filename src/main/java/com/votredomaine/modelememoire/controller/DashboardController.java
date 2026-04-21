package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.model.Enrollment;
import com.votredomaine.modelememoire.model.QuizResult;
import com.votredomaine.modelememoire.service.Courseservice;
import com.votredomaine.modelememoire.service.EnrollmentService;
import com.votredomaine.modelememoire.service.QuizResultService;
import com.votredomaine.modelememoire.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private EnrollmentService enrollmentService;
    
    @Autowired
    private QuizResultService quizResultService;
    
    @Autowired
    private Courseservice courseService;
    
    @Autowired
    private QuizService quizService;

    /**
     * Page dashboard étudiant
     * ⭐ MÉTHODE DÉPLACÉE ICI DEPUIS LoginController ⭐
     */
    @GetMapping("/student/dashboard")
    public ModelAndView studentDashboard(HttpSession session) {
        // Vérifier que l'utilisateur est un étudiant
        if (session.getAttribute("role") == null || !"STUDENT".equals(session.getAttribute("role"))) {
            return new ModelAndView("redirect:/login");
        }
        
        ModelAndView mav = new ModelAndView("htmlstudent/Dashboard");
        mav.addObject("userName", session.getAttribute("userName"));
        mav.addObject("studentName", session.getAttribute("userName"));
        mav.addObject("studentEmail", session.getAttribute("userEmail"));
        mav.addObject("niveau", session.getAttribute("niveau"));
        mav.addObject("filiere", session.getAttribute("filiere"));
        
        System.out.println("=== DASHBOARD ÉTUDIANT (DashboardController) ===");
        System.out.println("Étudiant: " + session.getAttribute("userName"));
        
        return mav;
    }
    
    /**
     * API pour récupérer les statistiques de l'étudiant (AJAX)
     */
    @GetMapping("/student/api/stats")
    @ResponseBody
    public Map<String, Object> getStudentStats(HttpSession session) {
        Map<String, Object> stats = new HashMap<>();
        
        Long userId = (Long) session.getAttribute("userId");
        String userName = (String) session.getAttribute("userName");
        
        System.out.println("=== RÉCUPÉRATION STATISTIQUES ÉTUDIANT ===");
        System.out.println("Étudiant: " + userName + " (ID: " + userId + ")");
        
        if (userId == null) {
            stats.put("success", false);
            stats.put("message", "Session expirée");
            return stats;
        }
        
        // 1. Nombre de cours actifs (téléchargés)
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(userId);
        int coursesDownloaded = enrollments.size();
        stats.put("coursesDownloaded", coursesDownloaded);
        stats.put("activeCourses", coursesDownloaded);
        
        // 2. Nombre de cours complétés
        long coursesCompleted = enrollments.stream()
            .filter(e -> e.getProgress() != null && e.getProgress() >= 100)
            .count();
        stats.put("coursesCompleted", (int) coursesCompleted);
        
        // 3. Nombre de quiz complétés
        List<QuizResult> quizResults = quizResultService.getResultsByStudentId(userId);
        int quizzesCompleted = quizResults.size();
        stats.put("quizzesCompleted", quizzesCompleted);
        stats.put("quizCompletes", quizzesCompleted);
        
        // 4. Quiz réussis
        long quizzesPassed = quizResults.stream().filter(QuizResult::getPassed).count();
        stats.put("quizzesPassed", quizzesPassed);
        
        // 5. Quiz disponibles
        List<Long> enrolledCourseIds = enrollments.stream()
            .map(Enrollment::getCourseId)
            .collect(Collectors.toList());
        
        long quizzesAvailable = 0;
        for (Long courseId : enrolledCourseIds) {
            quizzesAvailable += quizService.countQuizzesByCourse(courseId);
        }
        stats.put("quizzesAvailable", (int) quizzesAvailable);
        stats.put("quizDisponibles", (int) quizzesAvailable);
        
        // 6. Moyenne générale (sur 20)
        double averageGrade = quizResultService.getAverageScoreForStudent(userId);
        stats.put("averageGrade", Math.round(averageGrade * 10) / 10.0);
        stats.put("moyenne", Math.round(averageGrade * 10) / 10.0);
        
        // 7. Meilleur score
        double bestScore = quizResultService.getBestScoreForStudent(userId);
        stats.put("bestScore", Math.round(bestScore * 10) / 10.0);
        
        // 8. Heures d'étude estimées
        int studyHours = coursesDownloaded * 15;
        stats.put("studyHours", studyHours);
        stats.put("heuresEtude", studyHours);
        
        // 9. Dernier quiz complété
        QuizResult lastQuiz = quizResultService.getLastQuizResultForStudent(userId);
        if (lastQuiz != null) {
            stats.put("lastQuizTitle", lastQuiz.getQuizTitle());
            stats.put("lastQuizScore", lastQuiz.getPercentage());
        } else {
            stats.put("lastQuizTitle", "Aucun quiz");
            stats.put("lastQuizScore", 0);
        }
        
        // 10. Derniers cours téléchargés
        List<Course> recentCourses = enrollmentService.getRecentCoursesByStudent(userId, 4);
        stats.put("recentCourses", recentCourses);
        
        stats.put("success", true);
        
        System.out.println("Stats - Cours: " + coursesDownloaded + 
                          ", Quiz complétés: " + quizzesCompleted + 
                          ", Moyenne: " + averageGrade);
        
        return stats;
    }
}