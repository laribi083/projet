package com.votredomaine.modelememoire.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.Duration;

@Entity
@Table(name = "activities")
public class Activity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String type;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "user_name")
    private String userName;
    
    @Column(name = "user_role")
    private String userRole;
    
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
    
    // Getters
    public Long getId() { return id; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public String getUserName() { return userName; }
    public String getUserRole() { return userRole; }
    public Long getTargetId() { return targetId; }
    public String getTargetName() { return targetName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    // Setters
    public void setId(Long id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setMessage(String message) { this.message = message; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public void setTargetName(String targetName) { this.targetName = targetName; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getTimeAgo() {
        if (createdAt == null) return "Recently";
        
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(createdAt, now);
        
        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return days == 1 ? "1 day ago" : days + " days ago";
        } else if (hours > 0) {
            return hours == 1 ? "1 hour ago" : hours + " hours ago";
        } else if (minutes > 0) {
            return minutes == 1 ? "1 minute ago" : minutes + " minutes ago";
        } else {
            return "Just now";
        }
    }
}