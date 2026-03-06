package com.votredomaine.modelememoire.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    public void sendResetEmail(String to, String token) {
        // Implémentez l'envoi d'email ici
        // Utilisez JavaMailSender ou un service externe
        System.out.println("Email envoyé à " + to + " avec token: " + token);
    }
}