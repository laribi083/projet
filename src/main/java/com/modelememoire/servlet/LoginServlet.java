package com.modelememoire.servlet;

import com.modelememoire.dao.UtilisateurDAO;
import com.modelememoire.model.Utilisateur;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    
    private UtilisateurDAO utilisateurDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        utilisateurDAO = new UtilisateurDAO();
        gson = new Gson();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        PrintWriter out = resp.getWriter();
        JsonObject jsonResponse = new JsonObject();
        
        try {
            BufferedReader reader = req.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            
            JsonObject loginData = gson.fromJson(sb.toString(), JsonObject.class);
            String email = loginData.get("email").getAsString();
            String motDePasse = loginData.get("motDePasse").getAsString();
            
            // Validation
            if (email == null || email.trim().isEmpty()) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "L'email est requis");
                out.print(gson.toJson(jsonResponse));
                return;
            }
            
            if (motDePasse == null || motDePasse.length() < 6) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Mot de passe incorrect");
                out.print(gson.toJson(jsonResponse));
                return;
            }
            
            // Vérifier les identifiants
            Utilisateur utilisateur = utilisateurDAO.getUtilisateurParEmail(email);
            
            if (utilisateur != null && utilisateur.getMotDePasse().equals(motDePasse)) {
                // Créer une session
                HttpSession session = req.getSession();
                session.setAttribute("user", utilisateur);
                
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Connexion réussie!");
                jsonResponse.addProperty("email", utilisateur.getEmail());
            } else {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Email ou mot de passe incorrect");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Erreur serveur: " + e.getMessage());
        }
        
        out.print(gson.toJson(jsonResponse));
        out.flush();
    }
    
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
