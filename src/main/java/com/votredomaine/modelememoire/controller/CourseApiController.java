package com.votredomaine.modelememoire.controller;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.service.Courseservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseApiController {
    
    @Autowired
    private Courseservice courseService;
    
    @GetMapping("/{courseId}/content")
    public ResponseEntity<Map<String, Object>> getCourseContent(@PathVariable Long courseId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("📡 API appelée pour courseId: " + courseId);
            
            Course course = courseService.getCourseById(courseId);
            
            if (course == null) {
                response.put("success", false);
                response.put("error", "Cours non trouvé");
                return ResponseEntity.ok(response);
            }
            
            System.out.println("✅ Cours trouvé: " + course.getTitle());
            
            String content = "";
            
            // Récupérer le chemin du fichier
            String filePath = course.getFirstFilePath();
            if (filePath == null || filePath.isEmpty()) {
                filePath = course.getFilePath();
            }
            
            if (filePath != null && !filePath.isEmpty()) {
                Path path = Paths.get(filePath);
                System.out.println("📁 Chemin du fichier: " + path);
                
                if (Files.exists(path)) {
                    String fileName = course.getFirstFileName();
                    if (fileName == null) fileName = course.getFileName();
                    System.out.println("📄 Fichier trouvé: " + fileName);
                    System.out.println("📄 Taille du fichier: " + Files.size(path) + " bytes");
                    
                    // Vérifier l'extension du fichier
                    if (fileName != null && fileName.toLowerCase().endsWith(".pdf")) {
                        // Pour les PDF, afficher un message avec lien de téléchargement
                        content = "<div class='pdf-message' style='text-align: center; padding: 40px;'>" +
                                  "<div style='font-size: 4rem;'>📄</div>" +
                                  "<h3>" + escapeHtml(fileName) + "</h3>" +
                                  "<p>Ce document est au format PDF.</p>" +
                                  "<p>Taille: " + (Files.size(path) / 1024) + " KB</p>" +
                                  "<a href='/course/" + courseId + "/download' class='download-btn' style='display: inline-block; background: #28a745; color: white; padding: 12px 24px; border-radius: 8px; text-decoration: none; margin-top: 20px;'>" +
                                  "⬇️ Télécharger le PDF</a>" +
                                  "</div>";
                    } 
                    else if (fileName != null && (fileName.toLowerCase().endsWith(".txt") || 
                                                   fileName.toLowerCase().endsWith(".html") ||
                                                   fileName.toLowerCase().endsWith(".htm"))) {
                        // Pour les fichiers texte/HTML, lire le contenu
                        String fileContent = Files.readString(path);
                        content = "<pre style='white-space: pre-wrap; font-family: monospace; background: #f4f4f4; padding: 15px; border-radius: 8px;'>" + 
                                  escapeHtml(fileContent) + "</pre>";
                    }
                    else {
                        // Pour les autres types
                        content = "<div class='download-message' style='text-align: center; padding: 40px;'>" +
                                  "<div style='font-size: 4rem;'>📁</div>" +
                                  "<h3>" + escapeHtml(fileName) + "</h3>" +
                                  "<p>Ce fichier n'est pas affichable directement dans le navigateur.</p>" +
                                  "<a href='/course/" + courseId + "/download' class='download-btn' style='display: inline-block; background: #667eea; color: white; padding: 12px 24px; border-radius: 8px; text-decoration: none; margin-top: 20px;'>" +
                                  "⬇️ Télécharger le fichier</a>" +
                                  "</div>";
                    }
                } else {
                    System.out.println("❌ Fichier non trouvé: " + path);
                    content = "<div class='error-message'>❌ Fichier non trouvé sur le serveur</div>";
                }
            } else {
                content = "<div class='info-message'>📄 Aucun fichier associé à ce cours.</div>";
            }
            
            response.put("success", true);
            response.put("content", content);
            response.put("title", course.getTitle());
            response.put("courseId", courseId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("error", "Erreur: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}