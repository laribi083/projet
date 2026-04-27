package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.service.Courseservice;
import com.votredomaine.modelememoire.service.EnrollmentService;
import com.votredomaine.modelememoire.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class CourseController {

    @Autowired
    private Courseservice courseService;
    
    @Autowired
    private EnrollmentService enrollmentService;
    
    @Autowired
    private QuizService quizService;

    /**
     * Affiche les cours téléchargés par l'étudiant
     */
    @GetMapping("/niveux")
    public String showCourses(Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        Long userId = (Long) session.getAttribute("userId");
        
        System.out.println("=== AFFICHAGE DES COURS TÉLÉCHARGÉS ===");
        System.out.println("Utilisateur: " + userName + " (ID: " + userId + ")");
        
        if (userName == null || userId == null) {
            return "redirect:/login";
        }
        
        // Récupérer les cours téléchargés par l'étudiant
        List<Course> downloadedCourses = enrollmentService.getCoursesByStudent(userId);
        System.out.println("Cours téléchargés: " + downloadedCourses.size());
        
        model.addAttribute("courses", downloadedCourses);
        model.addAttribute("userName", userName);
        
        return "htmlstudent/niveux";
    }

    /**
     * ⭐ TÉLÉCHARGER UN COURS SUR LE BUREAU ⭐
     * Endpoint: /course/download/{courseId}
     */
    @GetMapping("/course/download/{courseId}")
    public ResponseEntity<?> downloadCourse(@PathVariable Long courseId, HttpSession session) {
        try {
            String userName = (String) session.getAttribute("userName");
            Long userId = (Long) session.getAttribute("userId");
            
            System.out.println("=== TÉLÉCHARGEMENT COURS ===");
            System.out.println("Course ID: " + courseId);
            System.out.println("Utilisateur: " + userName + " (ID: " + userId + ")");
            
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Utilisateur non connecté. Veuillez vous reconnecter.");
            }
            
            // Récupérer le cours
            Course course = courseService.getCourseById(courseId);
            if (course == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Cours non trouvé. Vérifiez l'ID du cours.");
            }
            
            // Vérifier si le cours est publié
            if (!"PUBLISHED".equals(course.getStatus()) && !"ACTIVE".equals(course.getStatus())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Ce cours n'est pas encore publié. Veuillez réessayer plus tard.");
            }
            
            // Enregistrer le téléchargement dans la base de données
            boolean isNewDownload = enrollmentService.registerDownload(userId, userName, courseId);
            
            if (isNewDownload) {
                System.out.println("✅ Nouveau téléchargement enregistré pour: " + course.getTitle());
            } else {
                System.out.println("⚠️ Cours déjà téléchargé précédemment par l'utilisateur");
            }
            
            // Vérifier si le cours a des fichiers
            if (course.getFilePaths() != null && !course.getFilePaths().isEmpty()) {
                // Télécharger le premier fichier du cours
                String filePath = course.getFilePaths().get(0);
                String fileName = course.getFileNames() != null && !course.getFileNames().isEmpty() 
                        ? course.getFileNames().get(0) : course.getTitle() + ".pdf";
                
                Path path = Paths.get(filePath);
                
                if (!Files.exists(path)) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Le fichier du cours n'est pas disponible sur le serveur.");
                }
                
                byte[] content = Files.readAllBytes(path);
                String mimeType = Files.probeContentType(path);
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }
                
                // Encoder le nom du fichier pour les caractères spéciaux
                String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                        .replaceAll("\\+", "%20");
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(mimeType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                        .body(content);
            } else {
                // Créer un fichier texte avec les informations du cours
                String courseInfo = generateCourseInfoFile(course);
                byte[] content = courseInfo.getBytes(StandardCharsets.UTF_8);
                String fileName = URLEncoder.encode(course.getTitle(), StandardCharsets.UTF_8)
                        .replaceAll("\\+", "%20") + "_info.txt";
                
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_PLAIN)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + fileName)
                        .body(content);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du téléchargement: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur inattendue: " + e.getMessage());
        }
    }
    
    /**
     * Génère un fichier texte avec les informations du cours
     */
    private String generateCourseInfoFile(Course course) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("        INFORMATIONS DU COURS          \n");
        sb.append("========================================\n\n");
        sb.append("Titre: ").append(course.getTitle()).append("\n");
        sb.append("Description: ").append(course.getDescription()).append("\n");
        sb.append("Module: ").append(course.getModule()).append("\n");
        sb.append("Niveau: ").append(course.getNiveau()).append("\n");
        sb.append("Professeur: ").append(course.getTeacherName()).append("\n");
        sb.append("Date de téléchargement: ").append(LocalDateTime.now()).append("\n");
        sb.append("\n========================================\n");
        sb.append("Ce cours a été téléchargé depuis BrainLearning\n");
        sb.append("Pour plus d'informations, connectez-vous à votre compte.\n");
        sb.append("========================================\n");
        return sb.toString();
    }
    
    /**
     * Afficher le contenu du cours après téléchargement
     */
    @GetMapping("/course/view/{courseId}")
    public String viewCourse(@PathVariable Long courseId, Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            return "redirect:/login";
        }
        
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return "redirect:/niveux";
        }
        
        // Vérifier si l'étudiant a téléchargé le cours
        boolean hasDownloaded = enrollmentService.hasDownloaded(userId, courseId);
        if (!hasDownloaded) {
            return "redirect:/niveux";
        }
        
        model.addAttribute("course", course);
        model.addAttribute("userName", userName);
        
        return "htmlstudent/course-viewer";
    }
}