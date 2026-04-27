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
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String module;
    private String niveau;
    private String status = "PENDING";
    
    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;
    
    @Column(name = "teacher_name")
    private String teacherName;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Champs pour les fichiers
    @Column(name = "file_path")
    private String filePath;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "file_type")
    private String fileType;
    
    @Column(name = "file_size")
    private String fileSize;
    
    @Column(name = "duration")
    private String duration;
    
    @Column(name = "html_content", columnDefinition = "LONGTEXT")
    private String htmlContent;
    
    @Column(name = "download_count")
    private Integer downloadCount = 0;
    
    @Column(name = "last_downloaded_at")
    private LocalDateTime lastDownloadedAt;
    
    // Collections pour les chemins de fichiers multiples
    @ElementCollection
    @CollectionTable(name = "course_file_paths", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "file_path")
    private List<String> filePaths = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "course_file_names", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "file_name")
    private List<String> fileNames = new ArrayList<>();
    
    // Champs pour les statistiques (non persistants)
    @Transient
    private Integer quizCount = 0;
    
    @Transient
    private Integer totalStudents = 0;
    
    // ⭐⭐⭐ CHAMPS POUR LES NOTES (RATINGS) - NON PERSISTANTS ⭐⭐⭐
    @Transient
    private Double averageRating;
    
    @Transient
    private Long ratingCount;
    
    @Transient
    private Boolean userHasRated;
    
    @Transient
    private Integer userRating;
    
    // ========== CONSTRUCTEURS ==========
    
    public Course() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.downloadCount = 0;
        this.filePaths = new ArrayList<>();
        this.fileNames = new ArrayList<>();
    }
    
    public Course(String title, String description, String module, String niveau) {
        this();
        this.title = title;
        this.description = description;
        this.module = module;
        this.niveau = niveau;
        this.status = "PENDING";
    }
    
    public Course(String title, String description, String niveau, String module, 
                  String teacherName, Long teacherId) {
        this();
        this.title = title;
        this.description = description;
        this.niveau = niveau;
        this.module = module;
        this.teacherName = teacherName;
        this.teacherId = teacherId;
        this.status = "PENDING";
    }
    
    // ==================== GETTERS ET SETTERS ====================
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    
    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
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
    
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    
    public String getHtmlContent() { return htmlContent; }
    public void setHtmlContent(String htmlContent) { this.htmlContent = htmlContent; }
    
    public Integer getDownloadCount() { return downloadCount != null ? downloadCount : 0; }
    public void setDownloadCount(Integer downloadCount) { this.downloadCount = downloadCount; }
    
    public LocalDateTime getLastDownloadedAt() { return lastDownloadedAt; }
    public void setLastDownloadedAt(LocalDateTime lastDownloadedAt) { this.lastDownloadedAt = lastDownloadedAt; }
    
    public List<String> getFilePaths() { return filePaths != null ? filePaths : new ArrayList<>(); }
    public void setFilePaths(List<String> filePaths) { 
        this.filePaths = filePaths != null ? filePaths : new ArrayList<>();
    }
    
    public List<String> getFileNames() { return fileNames != null ? fileNames : new ArrayList<>(); }
    public void setFileNames(List<String> fileNames) { 
        this.fileNames = fileNames != null ? fileNames : new ArrayList<>();
    }
    
    public Integer getQuizCount() { return quizCount != null ? quizCount : 0; }
    public void setQuizCount(Integer quizCount) { this.quizCount = quizCount; }
    
    public Integer getTotalStudents() { return totalStudents != null ? totalStudents : 0; }
    public void setTotalStudents(Integer totalStudents) { this.totalStudents = totalStudents; }
    
    // ==================== GETTERS/SETTERS POUR LES NOTES (RATINGS) ====================
    
    public Double getAverageRating() { 
        return averageRating != null ? averageRating : 0.0; 
    }
    public void setAverageRating(Double averageRating) { 
        this.averageRating = averageRating; 
    }
    
    public Long getRatingCount() { 
        return ratingCount != null ? ratingCount : 0L; 
    }
    public void setRatingCount(Long ratingCount) { 
        this.ratingCount = ratingCount; 
    }
    
    public Boolean getUserHasRated() { 
        return userHasRated != null ? userHasRated : false; 
    }
    public void setUserHasRated(Boolean userHasRated) { 
        this.userHasRated = userHasRated; 
    }
    
    public Integer getUserRating() { 
        return userRating != null ? userRating : 0; 
    }
    public void setUserRating(Integer userRating) { 
        this.userRating = userRating; 
    }
    
    // ==================== MÉTHODES UTILITAIRES ====================
    
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
    
    public boolean isPending() { 
        return "PENDING".equals(status); 
    }
    
    public boolean isValidated() { 
        return "VALIDATED".equals(status); 
    }
    
    public boolean isPublished() { 
        return "PUBLISHED".equals(status) || "ACTIVE".equals(status); 
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", module='" + module + '\'' +
                ", niveau='" + niveau + '\'' +
                ", status='" + status + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", averageRating=" + averageRating +
                ", ratingCount=" + ratingCount +
                '}';
    }
}