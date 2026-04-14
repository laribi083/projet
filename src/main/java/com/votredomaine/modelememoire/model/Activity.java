package com.votredomaine.modelememoire.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activities")
public class Activity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String type; // "COURSE_PUBLISHED", "USER_REGISTERED", "COURSE_UPDATED", "COURSE_DOWNLOADED"
    
    @Column(nullable = false)
    private String message;
    
    @Column(name = "user_name")
    private String userName;
    
    @Column(name = "user_role")
    private String userRole; // "TEACHER", "STUDENT", "ADMIN"
    
    @Column(name = "target_id")
    private Long targetId;
    
    @Column(name = "target_name")
    private String targetName;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public Activity() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Activity(String type, String message, String userName, String userRole) {
        this.type = type;
        this.message = message;
        this.userName = userName;
        this.userRole = userRole;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    
    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getTimeAgo() {
        if (createdAt == null) return "Récemment";
        
        LocalDateTime now = LocalDateTime.now();
        java.time.Duration duration = java.time.Duration.between(createdAt, now);
        
        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return days == 1 ? "Il y a 1 jour" : "Il y a " + days + " jours";
        } else if (hours > 0) {
            return hours == 1 ? "Il y a 1 heure" : "Il y a " + hours + " heures";
        } else if (minutes > 0) {
            return minutes == 1 ? "Il y a 1 minute" : "Il y a " + minutes + " minutes";
        } else {
            return "À l'instant";
        }
    }
}