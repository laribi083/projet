package com.votredomaine.modelememoire.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class forgetmodel {  // Renommé selon les conventions Java (classe commence par majuscule)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String resetToken;
    
    private LocalDateTime resetTokenExpiry;

    // Constructeur par défaut
    public forgetmodel() {
    }

    // Constructeur avec paramètres
    public forgetmodel(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
    
    public LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }

    // Méthode utilitaire pour vérifier si le token est expiré
    public boolean isResetTokenExpired() {
        if (resetTokenExpiry == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(resetTokenExpiry);
    }

    // Méthode pour générer un token avec expiration (à utiliser dans le service)
    public void generateResetToken() {
        this.resetToken = java.util.UUID.randomUUID().toString();
        this.resetTokenExpiry = LocalDateTime.now().plusHours(24); // Token valide 24h
    }

    @Override
    public String toString() {
        return "ForgetModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", resetToken='" + resetToken + '\'' +
                ", resetTokenExpiry=" + resetTokenExpiry +
                '}';
    }
}