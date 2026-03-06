package com.votredomaine.modelememoire.util;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class ValidationUtils {
    
    private static final String EMAIL_PATTERN = 
        "^[A-Za-z0-9+_.-]+@(.+)$";
    
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return pattern.matcher(email).matches();
    }
    
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= 100;
    }
}