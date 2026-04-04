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
    
    // ⭐ NOUVEAU CHAMP POUR LE CONTENU DU COURS
    @Column(columnDefinition = "LONGTEXT")
    private String content;
    
    private String imageUrl;
    
    @Column(name = "teacher_id")
    private Long teacherId;
    
    @Column(name = "teacher_name")
    private String teacherName;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    private String status = "ACTIVE";
    
    private String niveau;
    private String module;
    
    @Column(name = "total_hours")
    private Integer totalHours = 8;
    
    @ElementCollection
    @CollectionTable(name = "course_files", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "file_path")
    private List<String> filePaths = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "course_file_names", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "file_name")
    private List<String> fileNames = new ArrayList<>();
    
    private int totalStudents = 0;
    private int totalQuizzes = 0;
    
    public Course() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    // ⭐ GETTER/SETTER pour content
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    
    public Integer getTotalHours() { return totalHours; }
    public void setTotalHours(Integer totalHours) { this.totalHours = totalHours; }
    
    public List<String> getFilePaths() { return filePaths; }
    public void setFilePaths(List<String> filePaths) { this.filePaths = filePaths; }
    
    public List<String> getFileNames() { return fileNames; }
    public void setFileNames(List<String> fileNames) { this.fileNames = fileNames; }
    
    public int getTotalStudents() { return totalStudents; }
    public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
    
    public int getTotalQuizzes() { return totalQuizzes; }
    public void setTotalQuizzes(int totalQuizzes) { this.totalQuizzes = totalQuizzes; }
}