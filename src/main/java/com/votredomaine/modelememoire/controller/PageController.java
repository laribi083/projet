package com.votredomaine.modelememoire.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.HashMap;

@Controller
public class PageController {
    
    private final String API_URL = "http://localhost:8082/api/login";
    
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    @PostMapping("/login")
    public String processLogin(@RequestParam String username, 
                               @RequestParam String password,
                               HttpSession session) {
        try {
       
            RestTemplate restTemplate = new RestTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("email", username);
            requestBody.put("password", password);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                API_URL, 
                request, 
                Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                
                session.setAttribute("userId", responseBody.get("userId"));
                session.setAttribute("userName", responseBody.get("userName"));
                session.setAttribute("loggedIn", true);
                
                return "redirect:/dashboard";
            } else {
                return "redirect:/login?error=true";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/login?error=true";
        }
    }
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        // Vérifier si l'utilisateur est connecté
        if (session.getAttribute("loggedIn") == null) {
            return "redirect:/login";
        }
        return "htmlstudent/Dashboard";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Détruire la session
        return "redirect:/login";
    }
}