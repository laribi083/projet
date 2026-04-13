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
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    private String niveau;
    
    private String module;
    
    private String teacherName;
    
    @Column(nullable = false)
    private Long teacherId;
    
    private String duration;
    
    private String status;
    
    @Column(name = "last_downloaded_at")
    private LocalDateTime lastDownloadedAt;
    
    @Column(name = "download_count")
    private Integer downloadCount = 0;
    
    @ElementCollection
    @CollectionTable(name = "course_file_paths", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "file_path")
    private List<String> filePaths = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "course_file_names", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "file_name")
    private List<String> fileNames = new ArrayList<>();
    
    @Column(columnDefinition = "TEXT")
    private String htmlContent;
    
    private String filePath;
    private String fileName;
    private String fileType;
    private String fileSize;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Transient
    private Integer quizCount = 0;
    
    // ⭐ AJOUTER CE CHAMP
    @Transient
    private Integer totalStudents = 0;
    
    // ========== CONSTRUCTEURS ==========
    
    public Course() {}
    
    public Course(String title, String description, String niveau, String module, 
                  String teacherName, Long teacherId) {
        this.title = title;
        this.description = description;
        this.niveau = niveau;
        this.module = module;
        this.teacherName = teacherName;
        this.teacherId = teacherId;
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.downloadCount = 0;
    }
    
    // ========== GETTERS ET SETTERS ==========
    
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
    
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getLastDownloadedAt() { return lastDownloadedAt; }
    public void setLastDownloadedAt(LocalDateTime lastDownloadedAt) { this.lastDownloadedAt = lastDownloadedAt; }
    
    public Integer getDownloadCount() { return downloadCount != null ? downloadCount : 0; }
    public void setDownloadCount(Integer downloadCount) { this.downloadCount = downloadCount; }
    
    public List<String> getFilePaths() { return filePaths; }
    public void setFilePaths(List<String> filePaths) { 
        this.filePaths = filePaths != null ? filePaths : new ArrayList<>();
    }
    
    public List<String> getFileNames() { return fileNames; }
    public void setFileNames(List<String> fileNames) { 
        this.fileNames = fileNames != null ? fileNames : new ArrayList<>();
    }
    
    public String getHtmlContent() { return htmlContent; }
    public void setHtmlContent(String htmlContent) { this.htmlContent = htmlContent; }
    
    public String getFilePath() { 
        if (filePaths != null && !filePaths.isEmpty()) return filePaths.get(0);
        return filePath; 
    }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public String getFileName() { 
        if (fileNames != null && !fileNames.isEmpty()) return fileNames.get(0);
        return fileName; 
    }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    
    public String getFileSize() { return fileSize; }
    public void setFileSize(String fileSize) { this.fileSize = fileSize; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Integer getQuizCount() { return quizCount; }
    public void setQuizCount(Integer quizCount) { this.quizCount = quizCount; }
    
    // ⭐ GETTER ET SETTER POUR totalStudents
    public Integer getTotalStudents() { return totalStudents; }
    public void setTotalStudents(Integer totalStudents) { this.totalStudents = totalStudents; }
    
    // ========== MÉTHODES UTILITAIRES ==========
    
    public String getFirstFilePath() {
        if (filePaths != null && !filePaths.isEmpty()) return filePaths.get(0);
        return filePath;
    }
    
    public String getFirstFileName() {
        if (fileNames != null && !fileNames.isEmpty()) return fileNames.get(0);
        return fileName;
    }
    
    public String getFileTypeFromFile() {
        String name = getFirstFileName();
        if (name == null) return null;
        
        if (name.endsWith(".pdf")) return "PDF";
        if (name.endsWith(".html") || name.endsWith(".htm")) return "HTML";
        if (name.endsWith(".txt") || name.endsWith(".md")) return "TEXT";
        if (name.endsWith(".mp4") || name.endsWith(".webm")) return "VIDEO";
        if (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".gif")) return "IMAGE";
        return "DOCUMENT";
    }
    
    public void addFile(String filePath, String fileName) {
        if (this.filePaths == null) this.filePaths = new ArrayList<>();
        if (this.fileNames == null) this.fileNames = new ArrayList<>();
        this.filePaths.add(filePath);
        this.fileNames.add(fileName);
        if (this.filePath == null) {
            this.filePath = filePath;
            this.fileName = fileName;
        }
    }
    
    public void incrementDownloadCount() {
        if (this.downloadCount == null) {
            this.downloadCount = 1;
        } else {
            this.downloadCount++;
        }
        this.lastDownloadedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", niveau='" + niveau + '\'' +
                ", module='" + module + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", status='" + status + '\'' +
                ", downloadCount=" + downloadCount +
                '}';
    }
}