package com.votredomaine.modelememoire.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String description;
    private String niveau;
    private String module;
    private String teacherName;
    private Long teacherId;
    private Integer duration;
    private String status;
    
    @ElementCollection
    private List<String> filePaths = new ArrayList<>();
    
    @ElementCollection
    private List<String> fileNames = new ArrayList<>();
    
    @Column(columnDefinition = "TEXT")
    private String htmlContent;
    
    private String filePath;
    private String fileName;
    private String fileType;
    private Long fileSize;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Champs supplémentaires pour les statistiques (non persistants)
    @Transient
    private Integer totalQuizzes = 0;
    
    @Transient
    private Integer quizCount = 0;  // ⭐ AJOUTER CETTE LIGNE
    
    @Transient
    private Integer totalStudents = 0;
    
    // Constructeurs
    public Course() {}
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public List<String> getFilePaths() { return filePaths; }
    public void setFilePaths(List<String> filePaths) { this.filePaths = filePaths; }
    
    public List<String> getFileNames() { return fileNames; }
    public void setFileNames(List<String> fileNames) { this.fileNames = fileNames; }
    
    public String getHtmlContent() { return htmlContent; }
    public void setHtmlContent(String htmlContent) { this.htmlContent = htmlContent; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Integer getTotalQuizzes() { return totalQuizzes; }
    public void setTotalQuizzes(Integer totalQuizzes) { this.totalQuizzes = totalQuizzes; }
    
    public Integer getQuizCount() { return quizCount; }  // ⭐ AJOUTER
    public void setQuizCount(Integer quizCount) { this.quizCount = quizCount; }  // ⭐ AJOUTER
    
    public Integer getTotalStudents() { return totalStudents; }
    public void setTotalStudents(Integer totalStudents) { this.totalStudents = totalStudents; }
    
    // Méthodes utilitaires
    public String getFirstFilePath() {
        return (filePaths != null && !filePaths.isEmpty()) ? filePaths.get(0) : null;
    }
    
    public String getFirstFileName() {
        return (fileNames != null && !fileNames.isEmpty()) ? fileNames.get(0) : null;
    }
    
    public String getFileTypeFromFile() {
        String firstFileName = getFirstFileName();
        if (firstFileName != null && firstFileName.contains(".")) {
            String ext = firstFileName.substring(firstFileName.lastIndexOf(".") + 1).toUpperCase();
            switch (ext) {
                case "PDF": return "PDF";
                case "DOC": case "DOCX": return "WORD";
                case "PPT": case "PPTX": return "POWERPOINT";
                case "MP4": return "VIDEO";
                case "MP3": return "AUDIO";
                default: return "FILE";
            }
        }
        return "FILE";
    }
}