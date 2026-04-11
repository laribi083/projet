package com.votredomaine.modelememoire.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    public void sendResetEmail(String to, String token) {
        
        System.out.println("Email envoyé à " + to + " avec token: " + token);
    }
}