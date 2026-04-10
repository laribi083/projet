package com.votredomaine.modelememoire.service;

import com.votredomaine.modelememoire.model.Course;
import com.votredomaine.modelememoire.repository.courserepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class Courseservice {
    
    @Autowired
    private courserepository courseRepository;
    
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/courses/";
    
    // ========== MÉTHODES EXISTANTES ==========
    
    public List<Course> getCoursesByTeacherId(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }
    
    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElse(null);
    }
    
    public Optional<Course> findCourseById(Long id) {
        return courseRepository.findById(id);
    }
    
    public List<Course> getCoursesByNiveau(String niveau) {
        return courseRepository.findByNiveauAndStatus(niveau, "ACTIVE");
    }
    
    public List<Course> getAllActiveCourses() {
        return courseRepository.findByStatus("ACTIVE");
    }
    
    public Course createCourse(Course course, List<MultipartFile> files) throws IOException {
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        
        if (course.getStatus() == null) {
            course.setStatus("ACTIVE");
        }
        
        if (files != null && !files.isEmpty()) {
            List<String> filePaths = new ArrayList<>();
            List<String> fileNames = new ArrayList<>();
            
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String originalFileName = file.getOriginalFilename();
                    String fileName = UUID.randomUUID().toString() + "_" + originalFileName;
                    Path filePath = uploadPath.resolve(fileName);
                    
                    Files.copy(file.getInputStream(), filePath);
                    
                    filePaths.add(filePath.toString());
                    fileNames.add(originalFileName);
                }
            }
            
            course.setFilePaths(filePaths);
            course.setFileNames(fileNames);
            
            if (!filePaths.isEmpty()) {
                course.setFilePath(filePaths.get(0));
                course.setFileName(fileNames.get(0));
                
                String fileName = fileNames.get(0);
                if (fileName.endsWith(".pdf")) course.setFileType("PDF");
                else if (fileName.endsWith(".html") || fileName.endsWith(".htm")) course.setFileType("HTML");
                else if (fileName.endsWith(".txt")) course.setFileType("TEXT");
                else if (fileName.endsWith(".mp4")) course.setFileType("VIDEO");
                else course.setFileType("DOCUMENT");
            }
        }
        
        return courseRepository.save(course);
    }
    
    public Course updateCourse(Long id, Course courseDetails, List<MultipartFile> newFiles) throws IOException {
        Course existingCourse = courseRepository.findById(id).orElse(null);
        if (existingCourse == null) return null;
        
        existingCourse.setTitle(courseDetails.getTitle());
        existingCourse.setDescription(courseDetails.getDescription());
        existingCourse.setNiveau(courseDetails.getNiveau());
        existingCourse.setModule(courseDetails.getModule());
        existingCourse.setUpdatedAt(LocalDateTime.now());
        
        if (newFiles != null && !newFiles.isEmpty()) {
            List<String> filePaths = existingCourse.getFilePaths();
            List<String> fileNames = existingCourse.getFileNames();
            
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            for (MultipartFile file : newFiles) {
                if (!file.isEmpty()) {
                    String originalFileName = file.getOriginalFilename();
                    String fileName = UUID.randomUUID().toString() + "_" + originalFileName;
                    Path filePath = uploadPath.resolve(fileName);
                    
                    Files.copy(file.getInputStream(), filePath);
                    
                    filePaths.add(filePath.toString());
                    fileNames.add(originalFileName);
                }
            }
            
            existingCourse.setFilePaths(filePaths);
            existingCourse.setFileNames(fileNames);
        }
        
        return courseRepository.save(existingCourse);
    }
    
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id).orElse(null);
        if (course != null) {
            for (String filePath : course.getFilePaths()) {
                try {
                    Files.deleteIfExists(Paths.get(filePath));
                } catch (IOException e) {
                    System.err.println("Could not delete file: " + filePath);
                }
            }
            courseRepository.deleteById(id);
        }
    }
    
    public long getTotalCoursesByTeacher(Long teacherId) {
        return courseRepository.countByTeacherId(teacherId);
    }
    
    public List<Course> findByTeacherId(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }
    
    public List<Course> findByNiveau(String niveau) {
        return courseRepository.findByNiveau(niveau);
    }
    
    public List<Course> findByNiveauAndStatus(String niveau, String status) {
        return courseRepository.findByNiveauAndStatus(niveau, status);
    }
    
    public List<Course> findByModuleAndNiveau(String module, String niveau) {
        if (module == null || module.trim().isEmpty() || module.equals("all")) {
            return courseRepository.findByNiveau(niveau);
        }
        return courseRepository.findByModuleAndNiveau(module, niveau);
    }
    
    public List<Course> findByModuleAndNiveauAndStatus(String module, String niveau, String status) {
        if (module == null || module.trim().isEmpty() || module.equals("all")) {
            return courseRepository.findByNiveauAndStatus(niveau, status);
        }
        return courseRepository.findByModuleAndNiveauAndStatus(module, niveau, status);
    }
    
    public Course save(Course course) {
        if (course.getCreatedAt() == null) {
            course.setCreatedAt(LocalDateTime.now());
        }
        course.setUpdatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }
    
    public Course update(Course course) {
        course.setUpdatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }
    
    public List<Course> findAll() {
        return courseRepository.findAll();
    }
    
    public List<Course> findByStatus(String status) {
        return courseRepository.findByStatus(status);
    }
    
    // ========== NOUVELLES MÉTHODES ==========
    
    public List<Course> getAllCoursesForReceive() {
        return courseRepository.findAllActiveCoursesOrderByDate();
    }
    
    public String readCourseContent(Long courseId) throws IOException {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            throw new IOException("Course not found");
        }
        
        if (course.getHtmlContent() != null && !course.getHtmlContent().isEmpty()) {
            return course.getHtmlContent();
        }
        
        if (course.getFilePaths() != null && !course.getFilePaths().isEmpty()) {
            Path filePath = Paths.get(course.getFilePaths().get(0));
            if (Files.exists(filePath)) {
                String content = Files.readString(filePath);
                String fileName = course.getFileNames().get(0);
                
                if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
                    return content;
                } else if (fileName.endsWith(".txt") || fileName.endsWith(".md")) {
                    return "<pre style='white-space: pre-wrap; font-family: monospace;'>" + 
                           escapeHtml(content) + "</pre>";
                } else {
                    return generateDownloadMessage(course);
                }
            } else {
                return "<div class='error-message'>❌ Fichier non trouvé sur le serveur</div>";
            }
        }
        
        return "<div class='info-message'>📄 Aucun contenu disponible pour ce cours.</div>";
    }
    
    public boolean hasContent(Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) return false;
        
        return (course.getHtmlContent() != null && !course.getHtmlContent().isEmpty()) ||
               (course.getFilePaths() != null && !course.getFilePaths().isEmpty());
    }
    
    public List<Course> searchCourses(String status, String niveau, String module, String search) {
        return courseRepository.searchCourses(status, niveau, module, search);
    }
    
    public List<Course> findByModule(String module) {
        return courseRepository.findByModule(module);
    }
    
    public List<Course> findByTeacherName(String teacherName) {
        return courseRepository.findByTeacherNameContainingIgnoreCase(teacherName);
    }
    
    // ========== MÉTHODES UTILITAIRES PRIVÉES CORRIGÉES ==========
    
    private String generateDownloadMessage(Course course) {
        String fileName = course.getFileNames() != null && !course.getFileNames().isEmpty() 
                          ? course.getFileNames().get(0) : "fichier";
        long courseId = course.getId();
        return "<div class='download-message' style='text-align: center; padding: 40px;'>" +
               "<div style='font-size: 4rem;'>📄</div>" +
               "<h3>" + fileName + "</h3>" +
               "<p>Ce fichier n'est pas affichable directement dans le navigateur.</p>" +
               "<button onclick=\"window.location.href='/course/" + courseId + "/download'\" " +
               "style='background: #667eea; color: white; border: none; padding: 10px 20px; border-radius: 8px; cursor: pointer; margin-top: 20px;'>" +
               "⬇️ Télécharger le fichier" +
               "</button>" +
               "</div>";
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