package com.votredomaine.modelememoire.util;

import org.springframework.stereotype.Component;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class PasswordUtils {
    
    // Simple hash (pour la démo - utilisez BCrypt en production)
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    public static boolean checkPassword(String rawPassword, String hashedPassword) {
        String hashedRaw = hashPassword(rawPassword);
        return hashedRaw.equals(hashedPassword);
    }
}